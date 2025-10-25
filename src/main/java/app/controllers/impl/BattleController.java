package app.controllers.impl;

import app.config.HibernateConfig;
import app.daos.impl.BattleDAO;
import app.dtos.BattleDTO;
import app.entities.Battle;
import app.entities.BattleLog;
import app.entities.Hero;
import app.entities.Monster;
import app.controllers.IController;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple deterministic battle logic:
 * - Hero attacks first.
 * - Damage = max(1, attacker.attack - defender.defense)
 * - Alternate until one HP <= 0.
 * - XP awarded = max(0, monster.level * 5) on victory.
 */
public class BattleController implements IController<Battle, Integer> {

    private final BattleDAO dao;

    public BattleController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = BattleDAO.getInstance(emf);
    }

    public void startBattle(Context ctx) {
        StartRequest req = ctx.bodyAsClass(StartRequest.class);
        // Build minimal entities with ids (full managed copy will be loaded in DAO)
        Hero hero = new Hero();
        hero.setId(req.heroId);
        Monster monster = new Monster();
        monster.setId(req.monsterId);

        Battle battle = new Battle();
        battle.setHero(hero);
        battle.setMonster(monster);

        // Simple simulation
        int hHp = req.heroHp != null ? req.heroHp : 100;
        int hAtk = req.heroAttack != null ? req.heroAttack : 10;
        int hDef = req.heroDefense != null ? req.heroDefense : 5;

        int mHp = req.monsterHp != null ? req.monsterHp : 80;
        int mAtk = req.monsterAttack != null ? req.monsterAttack : 8;
        int mDef = req.monsterDefense != null ? req.monsterDefense : 4;

        int turn = 1;
        boolean heroTurn = true;
        while (hHp > 0 && mHp > 0) {
            if (heroTurn) {
                int dmg = Math.max(1, hAtk - mDef);
                mHp -= dmg;
                battle.addLog(new BattleLog(turn, req.heroName != null ? req.heroName : "Hero", req.monsterName != null ? req.monsterName : "Monster", dmg, Math.max(0, mHp)));
            } else {
                int dmg = Math.max(1, mAtk - hDef);
                hHp -= dmg;
                battle.addLog(new BattleLog(turn, req.monsterName != null ? req.monsterName : "Monster", req.heroName != null ? req.heroName : "Hero", dmg, Math.max(0, hHp)));
            }
            heroTurn = !heroTurn;
            turn++;
        }

        if (mHp <= 0 && hHp > 0) {
            battle.setResult("VICTORY");
            battle.setXpGained(Math.max(0, req.monsterLevel != null ? req.monsterLevel * 5 : 5));
        } else if (hHp <= 0 && mHp > 0) {
            battle.setResult("DEFEAT");
            battle.setXpGained(0);
        } else {
            battle.setResult("DRAW");
            battle.setXpGained(0);
        }

        Battle persisted = dao.create(battle);
        ctx.status(201);
        ctx.json(new BattleDTO(persisted));
    }

    @Override
    public void readAll(Context ctx) {
        List<Battle> list = dao.readAll();
        List<BattleDTO> dtoList = list.stream().map(BattleDTO::new).collect(Collectors.toList());
        ctx.status(200);
        ctx.json(dtoList);
    }

    @Override
    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Battle b = dao.read(id);
        ctx.status(b != null ? 200 : 404);
        ctx.json(b != null ? new BattleDTO(b) : "{ \"status\": 404, \"msg\": \"Not found\" }");
    }

    @Override
    public void create(Context ctx) {
        // delegate to startBattle which contains the battle simulation and persistence
        startBattle(ctx);
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Battle incoming = ctx.bodyAsClass(Battle.class);
        Battle updated = dao.update(id, incoming);
        ctx.status(updated != null ? 200 : 404);
        ctx.json(updated != null ? new BattleDTO(updated) : "{ \"status\": 404, \"msg\": \"Not found\" }");
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        dao.delete(id);
        ctx.status(200);
        ctx.json("{ \"msg\": \"Battle deleted\" }");
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return dao.validatePrimaryKey(id);
    }

    @Override
    public Battle validateEntity(Context ctx) {
        // Accept either a Battle JSON or StartRequest JSON -> try StartRequest first
        try {
            StartRequest req = ctx.bodyAsClass(StartRequest.class);
            Hero h = new Hero();
            h.setId(req.heroId);
            Monster m = new Monster();
            m.setId(req.monsterId);
            Battle b = new Battle();
            b.setHero(h);
            b.setMonster(m);
            return b;
        } catch (Exception e) {
            return ctx.bodyAsClass(Battle.class);
        }
    }

    // Request helper class
    public static class StartRequest {
        public Integer heroId;
        public Integer monsterId;
        public String area;

        // Optional detailed stats to simulate without DB lookups
        public Integer heroHp;
        public Integer heroAttack;
        public Integer heroDefense;
        public String heroName;
        public Integer monsterHp;
        public Integer monsterAttack;
        public Integer monsterDefense;
        public String monsterName;
        public Integer monsterLevel;
    }
}
