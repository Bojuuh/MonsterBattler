package app.daos.impl;

import app.entities.Monster;
import app.dtos.MonsterDTO;
import app.daos.IDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MonsterDAO implements IDAO<Monster, Integer> {

    private static MonsterDAO instance;
    private static EntityManagerFactory emf;

    public static MonsterDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new MonsterDAO();
        }
        return instance;
    }

    @Override
    public Monster create(Monster m) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(m);
            em.getTransaction().commit();
            return m;
        }
    }

    @Override
    public Monster read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Monster.class, id);
        }
    }

    @Override
    public List<Monster> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Monster> q = em.createQuery("SELECT m FROM Monster m", Monster.class);
            return q.getResultList();
        }
    }

    @Override
    public Monster update(Integer id, Monster updated) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Monster m = em.find(Monster.class, id);
            if (m == null) {
                em.getTransaction().commit();
                return null;
            }
            m.setName(updated.getName());
            m.setLevel(updated.getLevel());
            m.setHp(updated.getHp());
            m.setAttack(updated.getAttack());
            m.setDefense(updated.getDefense());
            m.setArea(updated.getArea());
            m.setSpawnWeight(updated.getSpawnWeight());
            Monster merged = em.merge(m);
            em.getTransaction().commit();
            return merged;
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Monster m = em.find(Monster.class, id);
            if (m != null) em.remove(m);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return id != null && id > 0;
    }

    public void populate() {
        try (var em = emf.createEntityManager()){
            em
                    .getTransaction()
                    .begin();
            Monster m1 = new Monster("Goblin", 1, 60, 8, 3, "Caves", 30);
            Monster m2 = new Monster("Wolf", 1, 70, 10, 4, "Forest", 25);
            Monster m3 = new Monster("Orc Brute", 2, 120, 14, 7, "Hills", 15);
            Monster m4 = new Monster("Stone Golem", 4, 250, 20, 18, "Ruins", 5);

            em.persist(m1);
            em.persist(m2);
            em.persist(m3);
            em.persist(m4);

            em
                    .getTransaction()
                    .commit();
        }
    }
}
