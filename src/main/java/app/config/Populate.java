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
        HeroDAO heroDao = HeroDAO.getInstance(emf);
        MonsterDAO monsterDao = MonsterDAO.getInstance(emf);

        List<Hero> existingHeroes = heroDao.readAll();
        List<Monster> existingMonsters = monsterDao.readAll();

        if (!existingHeroes.isEmpty() || !existingMonsters.isEmpty()) {
            System.out.println("Database already contains heroes or monsters - skipping populate.");
            return;
        }

        // Heroes
        Hero h1 = new Hero("Sir Gallant", 1, 100, 12, 6, 0);
        Hero h2 = new Hero("Aria Swift", 2, 120, 15, 8, 10);
        Hero h3 = new Hero("Dorn Ironfist", 3, 150, 18, 12, 30);

        heroDao.create(h1);
        heroDao.create(h2);
        heroDao.create(h3);

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
