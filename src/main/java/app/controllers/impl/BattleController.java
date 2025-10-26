package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.BattleDAO;
import app.daos.impl.HeroDAO;
import app.daos.impl.MonsterDAO;
import app.dtos.BattleDTO;
import app.dtos.BattleLogDTO;
import app.dtos.BattleResultDTO;
import app.entities.Battle;
import app.entities.BattleLog;
import app.entities.Hero;
import app.entities.Monster;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.Comparator;

public class BattleController implements IController<Battle, Integer> {
    private final BattleDAO battleDao;
    private final HeroDAO heroDao;
    private final MonsterDAO monsterDao;

    public BattleController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.battleDao = BattleDAO.getInstance(emf);
        this.heroDao = HeroDAO.getInstance(emf);
        this.monsterDao = MonsterDAO.getInstance(emf);
    }

    public void startBattle(Context ctx) {
        var input = ctx.bodyAsClass(BattleStartDTO.class);
        Hero hero = heroDao.read(input.heroId());
        Monster monster = monsterDao.read(input.monsterId());

        if (hero == null || monster == null) {
            ctx.status(404).json("Hero or monster not found");
            return;
        }

        Battle battle = simulateBattle(hero, monster);
        Battle savedBattle = battleDao.create(battle);

        if("VICTORY".equals(battle.getResult())){
            hero.setXp(hero.getXp() + battle.getXpGained());
            heroDao.update(hero.getId(), hero);
        }

        ctx.status(201).json(new BattleDTO(savedBattle));
    }

    private Battle simulateBattle(Hero hero, Monster monster) {
        Battle battle = new Battle();
        battle.setHero(hero);
        battle.setMonster(monster);

        int heroHp = hero.getHp();
        int monsterHp = monster.getHp();
        int turn = 1;
        boolean heroTurn = true;

        while (heroHp > 0 && monsterHp > 0) {
            if (heroTurn) {
                int damage = Math.max(1, hero.getAttack() - monster.getDefense());
                monsterHp -= damage;
                battle.addLog(new BattleLog(turn, hero.getName(), monster.getName(), damage, Math.max(0, monsterHp)));
            } else {
                int damage = Math.max(1, monster.getAttack() - hero.getDefense());
                heroHp -= damage;
                battle.addLog(new BattleLog(turn, monster.getName(), hero.getName(), damage, Math.max(0, heroHp)));
            }
            heroTurn = !heroTurn;
            turn++;
        }

        determineBattleResult(battle, heroHp, monsterHp, monster.getLevel());
        return battle;
    }


    private void determineBattleResult(Battle battle, int heroHp, int monsterHp, int monsterLevel) {
        if (monsterHp <= 0 && heroHp > 0) {
            battle.setResult("VICTORY");
            battle.setXpGained(Math.max(0, monsterLevel * 5));
        } else if (heroHp <= 0 && monsterHp > 0) {
            battle.setResult("DEFEAT");
            battle.setXpGained(0);
        } else {
            battle.setResult("DRAW");
            battle.setXpGained(0);
        }
    }

    @Override
    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Battle battle = battleDao.read(id);
        if (battle == null) {
            ctx.status(404).json("Battle not found");
            return;
        }
        ctx.json(new BattleDTO(battle));
    }

    public void getBattleDetails(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Battle battle = battleDao.read(id);
        if (battle == null) {
            ctx.status(404).json("Battle not found");
            return;
        }

        var logs = battle.getLogs().stream()
                .sorted(Comparator.comparing(BattleLog::getTurnNumber))
                .map(log -> new BattleLogDTO(
                        log.getTurnNumber(),
                        log.getAttacker(),
                        log.getDefender(),
                        log.getDamage(),
                        log.getDefenderHpAfter()
                )).toList();

        ctx.json(new BattleResultDTO(
                battle.getId(),
                battle.getHero().getName(),
                battle.getMonster().getName(),
                battle.getResult(),
                battle.getXpGained(),
                logs.size(),
                logs
        ));
    }

    @Override
    public void readAll(Context ctx) {
        var battles = battleDao.readAll().stream()
                .map(BattleDTO::new)
                .toList();
        ctx.json(battles);
    }

    @Override
    public void create(Context ctx) {
        ctx.status(405).json("Use /battles/start to create a battle");
    }

    @Override
    public void update(Context ctx) {
        ctx.status(405).json("Battles cannot be updated");
    }

    @Override
    public void delete(Context ctx) {
        ctx.status(405).json("Battles cannot be deleted");
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return battleDao.validatePrimaryKey(id);
    }

    @Override
    public Battle validateEntity(Context ctx) {
        return null; // Not needed as battles are created through startBattle
    }

    private record BattleStartDTO(Integer heroId, Integer monsterId) {}
}
