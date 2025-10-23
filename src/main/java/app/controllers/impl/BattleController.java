package app.controllers.impl;

import app.config.HibernateConfig;
import app.daos.impl.BattleDAO;
import app.dtos.BattleDTO;
import app.entities.Battle;
import app.entities.BattleLog;
import app.entities.Hero;
import app.entities.Monster;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

/**
 * Simple deterministic battle logic:
 * - Hero attacks first.
 * - Damage = max(1, attacker.attack - defender.defense)
 * - Alternate until one HP <= 0.
 * - XP awarded = max(0, monster.level * 5) on victory.
 */
public class BattleController {

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

        Battle persisted = dao.persistBattle(battle);
        ctx.status(201);
        ctx.json(new BattleDTO(persisted));
    }

    public void readAll(Context ctx) {
        List<BattleDTO> list = dao.readAll();
        ctx.status(200);
        ctx.json(list);
    }

    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Battle b = dao.read(id);
        ctx.status(b != null ? 200 : 404);
        ctx.json(b != null ? new BattleDTO(b) : "{ \"status\": 404, \"msg\": \"Not found\" }");
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
