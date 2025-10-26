// java
package app.simulators;

import app.dtos.BattleDTO;
import app.entities.Battle;
import app.entities.BattleLog;
import app.entities.Hero;
import app.entities.Monster;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;

public class BattleSimulator {

    public static void main(String[] args) throws Exception {
        // Create hero and monster
        Hero hero = new Hero("Sir Gallant", 1, 100, 12, 6, 0);
        hero.setId(1);
        Monster monster = new Monster("Goblin", 1, 80, 8, 4, "Caves", 30);
        monster.setId(2);

        Battle battle = new Battle();
        battle.setHero(hero);
        battle.setMonster(monster);

        // Stats for simulation
        int hHp = hero.getHp();
        int hAtk = hero.getAttack();
        int hDef = hero.getDefense();

        int mHp = monster.getHp();
        int mAtk = monster.getAttack();
        int mDef = monster.getDefense();

        int turn = 1;
        boolean heroTurn = true;

        // Attack loop
        while (hHp > 0 && mHp > 0) {
            if (heroTurn) {
                int dmg = Math.max(1, hAtk - mDef);
                mHp -= dmg;
                battle.addLog(new BattleLog(turn, hero.getName(), monster.getName(), dmg, Math.max(0, mHp)));
            } else {
                int dmg = Math.max(1, mAtk - hDef);
                hHp -= dmg;
                battle.addLog(new BattleLog(turn, monster.getName(), hero.getName(), dmg, Math.max(0, hHp)));
            }
            heroTurn = !heroTurn;
            turn++;
        }

        // Result and XP
        if (mHp <= 0 && hHp > 0) {
            battle.setResult("VICTORY");
            battle.setXpGained(Math.max(0, monster.getLevel() * 5));
        } else if (hHp <= 0 && mHp > 0) {
            battle.setResult("DEFEAT");
            battle.setXpGained(0);
        } else {
            battle.setResult("DRAW");
            battle.setXpGained(0);
        }

        BattleDTO dto = new BattleDTO(battle);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        System.out.println(json);
    }
}
