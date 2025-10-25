package app.daos.impl;

import app.entities.Battle;
import app.entities.BattleLog;
import app.entities.Hero;
import app.entities.Monster;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BattleDAO implements app.daos.IDAO<Battle, Integer> {

    private static BattleDAO instance;
    private static EntityManagerFactory emf;

    public static BattleDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new BattleDAO();
        }
        return instance;
    }

    @Override
    public Battle create(Battle battle) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // ensure hero/monster are managed
            Hero hero = em.find(Hero.class, battle.getHero().getId());
            Monster monster = em.find(Monster.class, battle.getMonster().getId());
            battle.setHero(hero);
            battle.setMonster(monster);
            battle.setBattleDate(Instant.now());
            em.persist(battle);
            em.getTransaction().commit();
            return battle;
        }
    }

    @Override
    public Battle read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Battle.class, id);
        }
    }

    @Override
    public List<Battle> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Battle> q = em.createQuery("SELECT b FROM Battle b", Battle.class);
            return q.getResultList();
        }
    }

    @Override
    public Battle update(Integer id, Battle updated) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Battle b = em.find(Battle.class, id);
            if (b == null) {
                em.getTransaction().commit();
                return null;
            }
            b.setHero(updated.getHero());
            b.setMonster(updated.getMonster());
            b.setResult(updated.getResult());
            b.setXpGained(updated.getXpGained());
            Battle merged = em.merge(b);
            em.getTransaction().commit();
            return merged;
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Battle b = em.find(Battle.class, id);
            if (b != null) em.remove(b);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return id != null && id > 0;
    }
}
