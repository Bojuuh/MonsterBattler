// java
package app.config;

import app.config.HibernateConfig;
import app.daos.impl.HeroDAO;
import app.daos.impl.MonsterDAO;
import app.entities.Hero;
import app.entities.Monster;

import java.util.List;

/**
 * Simple DB populator for heroes and monsters.
 * Call Populate.populate() at startup or run the main method to seed the DB.
 */
public class Populate {

    public static void populate() {
        var emf = HibernateConfig.getEntityManagerFactory();
        MonsterDAO monsterDao = MonsterDAO.getInstance(emf);

        List<Monster> existingMonsters = monsterDao.readAll();

        if (!existingMonsters.isEmpty()) {
            System.out.println("Database already contains monsters - skipping populate.");
            return;
        }

        // Monsters
        Monster m1 = new Monster("Goblin", 1, 60, 8, 3, "Caves", 30);
        Monster m2 = new Monster("Wolf", 1, 70, 10, 4, "Forest", 25);
        Monster m3 = new Monster("Orc Brute", 2, 120, 14, 7, "Hills", 15);
        Monster m4 = new Monster("Stone Golem", 4, 250, 20, 18, "Ruins", 5);

        monsterDao.create(m1);
        monsterDao.create(m2);
        monsterDao.create(m3);
        monsterDao.create(m4);

        System.out.println("Populated heroes and monsters into the database.");
    }

    public static void main(String[] args) {
        populate();
        System.out.println("Populate finished.");
    }
}
