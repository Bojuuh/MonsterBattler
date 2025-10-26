// java
package app.daos.impl;

import app.config.HibernateConfig;
import app.entities.Hero;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeroDAOTest {

    private EntityManagerFactory emf;
    private HeroDAO dao;

    @BeforeEach
    void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = HeroDAO.getInstance(emf);
        clearDb();
    }

    @AfterEach
    void tearDown() {
        clearDb();
    }

    private void clearDb() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // order matters due to FK constraints in other tests
            em.createQuery("DELETE FROM app.entities.BattleLog").executeUpdate();
            em.createQuery("DELETE FROM app.entities.Battle").executeUpdate();
            em.createQuery("DELETE FROM app.entities.Hero").executeUpdate();
            em.createQuery("DELETE FROM app.entities.Monster").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    void createAndRead() {
        Hero in = new Hero("TestHero", 1, 100, 10, 5, 0);
        Hero created = dao.create(in);
        assertNotNull(created.getId());
        Hero fetched = dao.read(created.getId());
        assertNotNull(fetched);
        assertEquals("TestHero", fetched.getName());
    }

    @Test
    void readAll() {
        dao.create(new Hero("H1", 1, 50, 5, 2, 0));
        dao.create(new Hero("H2", 2, 80, 8, 3, 0));
        List<Hero> list = dao.readAll();
        assertTrue(list.size() >= 2);
        assertTrue(list.stream().anyMatch(h -> "H1".equals(h.getName())));
        assertTrue(list.stream().anyMatch(h -> "H2".equals(h.getName())));
    }

    @Test
    void update() {
        Hero created = dao.create(new Hero("Upd", 1, 60, 6, 3, 0));
        created.setName("UpdatedName");
        created.setHp(77);
        Hero updated = dao.update(created.getId(), created);
        assertNotNull(updated);
        assertEquals("UpdatedName", updated.getName());
        assertEquals(77, updated.getHp());
    }

    @Test
    void delete() {
        Hero created = dao.create(new Hero("ToDelete", 1, 40, 4, 2, 0));
        Integer id = created.getId();
        dao.delete(id);
        assertNull(dao.read(id));
    }

    @Test
    void validatePrimaryKey() {
        assertTrue(dao.validatePrimaryKey(1));
        assertFalse(dao.validatePrimaryKey(0));
        assertFalse(dao.validatePrimaryKey(null));
    }
}
