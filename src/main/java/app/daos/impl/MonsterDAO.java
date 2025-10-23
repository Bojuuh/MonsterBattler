package app.daos.impl;

import app.entities.Monster;
import app.dtos.MonsterDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MonsterDAO {

    private static MonsterDAO instance;
    private static EntityManagerFactory emf;

    public static MonsterDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new MonsterDAO();
        }
        return instance;
    }

    public Monster create(Monster m) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(m);
            em.getTransaction().commit();
            return m;
        }
    }

    public Monster read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Monster.class, id);
        }
    }

    public List<MonsterDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<MonsterDTO> q = em.createQuery("SELECT new app.dtos.MonsterDTO(m) FROM Monster m", MonsterDTO.class);
            return q.getResultList();
        }
    }

    public Monster update(Integer id, Monster updated) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Monster m = em.find(Monster.class, id);
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

    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Monster m = em.find(Monster.class, id);
            if (m != null) em.remove(m);
            em.getTransaction().commit();
        }
    }
}
