// java
package app.daos.impl;

import app.config.HibernateConfig;
import app.entities.Monster;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonsterDAOTest {

    private EntityManagerFactory emf;
    private MonsterDAO dao;

    @BeforeEach
    void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = MonsterDAO.getInstance(emf);
        clearDb();
    }

    @AfterEach
    void tearDown() {
        clearDb();
    }

    private void clearDb() {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM app.entities.BattleLog").executeUpdate();
            em.createQuery("DELETE FROM app.entities.Battle").executeUpdate();
            em.createQuery("DELETE FROM app.entities.Hero").executeUpdate();
            em.createQuery("DELETE FROM app.entities.Monster").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    void createAndRead() {
        Monster m = new Monster("Gob", 1, 30, 5, 2, "Cave", 10);
        Monster created = dao.create(m);
        assertNotNull(created.getId());
        Monster fetched = dao.read(created.getId());
        assertNotNull(fetched);
        assertEquals("Gob", fetched.getName());
    }

    @Test
    void readAll() {
        dao.create(new Monster("M1", 1, 20, 3, 1, "A", 5));
        dao.create(new Monster("M2", 2, 50, 7, 4, "B", 2));
        List<Monster> all = dao.readAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    void update() {
        Monster created = dao.create(new Monster("Old", 1, 20, 3, 1, "X", 5));
        created.setName("NewName");
        created.setHp(99);
        Monster updated = dao.update(created.getId(), created);
        assertNotNull(updated);
        assertEquals("NewName", updated.getName());
        assertEquals(99, updated.getHp());
    }

    @Test
    void delete() {
        Monster created = dao.create(new Monster("Del", 1, 10, 2, 1, "Z", 1));
        Integer id = created.getId();
        dao.delete(id);
        assertNull(dao.read(id));
    }

    @Test
    void populateAndValidatePrimaryKey() {
        dao.populate();
        List<Monster> all = dao.readAll();
        assertTrue(all.size() >= 4, "populate should add default monsters");
        assertTrue(dao.validatePrimaryKey(1));
        assertFalse(dao.validatePrimaryKey(0));
        assertFalse(dao.validatePrimaryKey(null));
    }
}
