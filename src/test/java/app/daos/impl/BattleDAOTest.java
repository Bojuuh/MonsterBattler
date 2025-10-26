// java
package app.daos.impl;

import app.config.HibernateConfig;
import app.entities.Battle;
import app.entities.Hero;
import app.entities.Monster;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BattleDAOTest {

    private EntityManagerFactory emf;
    private BattleDAO battleDao;
    private HeroDAO heroDao;
    private MonsterDAO monsterDao;

    @BeforeEach
    void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        battleDao = BattleDAO.getInstance(emf);
        heroDao = HeroDAO.getInstance(emf);
        monsterDao = MonsterDAO.getInstance(emf);
        clearDb();
    }

    @AfterEach
    void tearDown() {
        clearDb();
    }

    private void clearDb() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM app.entities.BattleLog").executeUpdate();
            em.createQuery("DELETE FROM app.entities.Battle").executeUpdate();
            em.createQuery("DELETE FROM app.entities.Hero").executeUpdate();
            em.createQuery("DELETE FROM app.entities.Monster").executeUpdate();
            em.getTransaction().commit();
        }
    }

    private Hero createHero() {
        return heroDao.create(new Hero("BHero", 1, 100, 10, 5, 0));
    }

    private Monster createMonster() {
        return monsterDao.create(new Monster("BMonster", 1, 50, 8, 3, "Cave", 10));
    }

    @Test
    void createAndRead() {
        Hero h = createHero();
        Monster m = createMonster();

        Battle b = new Battle();
        b.setHero(h);
        b.setMonster(m);
        Battle created = battleDao.create(b);
        assertNotNull(created.getId());
        Battle fetched = battleDao.read(created.getId());
        assertNotNull(fetched);
        assertEquals(h.getId(), fetched.getHero().getId());
        assertEquals(m.getId(), fetched.getMonster().getId());
    }

    @Test
    void readAll() {
        Hero h = createHero();
        Monster m = createMonster();
        Battle b = new Battle();
        b.setHero(h);
        b.setMonster(m);
        battleDao.create(b);
        List<Battle> all = battleDao.readAll();
        assertTrue(all.size() >= 1);
    }

    @Test
    void update() {
        Hero h = createHero();
        Monster m = createMonster();
        Battle b = new Battle();
        b.setHero(h);
        b.setMonster(m);
        Battle created = battleDao.create(b);

        Battle updatedPayload = new Battle();
        updatedPayload.setHero(h);
        updatedPayload.setMonster(m);
        updatedPayload.setResult("VICTORY");
        updatedPayload.setXpGained(10);

        Battle updated = battleDao.update(created.getId(), updatedPayload);
        assertNotNull(updated);
        assertEquals("VICTORY", updated.getResult());
        assertEquals(10, updated.getXpGained());
    }

    @Test
    void delete() {
        Hero h = createHero();
        Monster m = createMonster();
        Battle b = new Battle();
        b.setHero(h);
        b.setMonster(m);
        Battle created = battleDao.create(b);
        Integer id = created.getId();
        battleDao.delete(id);
        assertNull(battleDao.read(id));
    }

    @Test
    void validatePrimaryKey() {
        assertTrue(battleDao.validatePrimaryKey(1));
        assertFalse(battleDao.validatePrimaryKey(0));
        assertFalse(battleDao.validatePrimaryKey(null));
    }
}
