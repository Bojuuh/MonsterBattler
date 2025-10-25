package app.daos.impl;

import app.dtos.HeroDTO;
import app.entities.Hero;
import app.daos.IDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HeroDAO implements IDAO<Hero, Integer> {

    private static HeroDAO instance;
    private static EntityManagerFactory emf;

    public static HeroDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HeroDAO();
        }
        return instance;
    }

    @Override
    public Hero create(Hero hero) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(hero);
            em.getTransaction().commit();
            return hero;
        }
    }

    @Override
    public Hero read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Hero.class, id);
        }
    }

    @Override
    public List<Hero> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Hero> q = em.createQuery("SELECT h FROM Hero h", Hero.class);
            return q.getResultList();
        }
    }

    @Override
    public Hero update(Integer id, Hero updated) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hero h = em.find(Hero.class, id);
            if (h == null) {
                em.getTransaction().commit();
                return null;
            }
            h.setName(updated.getName());
            h.setLevel(updated.getLevel());
            h.setHp(updated.getHp());
            h.setAttack(updated.getAttack());
            h.setDefense(updated.getDefense());
            h.setXp(updated.getXp());
            Hero merged = em.merge(h);
            em.getTransaction().commit();
            return merged;
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hero h = em.find(Hero.class, id);
            if (h != null) em.remove(h);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return id != null && id > 0;
    }
}
