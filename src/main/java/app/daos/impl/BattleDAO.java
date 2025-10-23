package app.daos.impl;

import app.entities.Battle;
import app.entities.BattleLog;
import app.entities.Hero;
import app.entities.Monster;
import app.dtos.BattleDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BattleDAO {

    private static BattleDAO instance;
    private static EntityManagerFactory emf;

    public static BattleDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new BattleDAO();
        }
        return instance;
    }

    public Battle persistBattle(Battle battle) {
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

    public Battle read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Battle.class, id);
        }
    }

    public List<BattleDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<BattleDTO> q = em.createQuery("SELECT new app.dtos.BattleDTO(b) FROM Battle b", BattleDTO.class);
            return q.getResultList();
        }
    }
}
