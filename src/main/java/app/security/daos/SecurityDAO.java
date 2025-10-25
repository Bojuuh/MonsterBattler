package app.security.daos;

import app.config.HibernateConfig;
import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;

public class SecurityDAO implements ISecurityDAO {

    private static SecurityDAO instance;
    private static EntityManagerFactory emf;

    private SecurityDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    public static SecurityDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            instance = new SecurityDAO(_emf);
        }
        return instance;
    }

    @Override
    public User getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :u", User.class);
            q.setParameter("u", username);
            User user;
            try {
                user = q.getSingleResult();
            } catch (NoResultException e) {
                throw new ValidationException("Invalid username or password");
            }
            if (user.verifyPassword(password)) {
                return user;
            } else {
                throw new ValidationException("Invalid username or password");
            }
        }
    }

    @Override
    public User createUser(String username, String password) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Check if exists
            User exists = em.find(User.class, username);
            if (exists != null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("User already exists");
            }
            User newUser = new User(username, password); // constructor already hashes password
            // Add default role STANDARD (create if missing)
            Role defaultRole = em.find(Role.class, "STANDARD");
            if (defaultRole == null) {
                defaultRole = new Role("STANDARD");
                em.persist(defaultRole);
            }
            newUser.addRole(defaultRole);
            em.persist(newUser);
            em.getTransaction().commit();
            return newUser;
        }
    }

    @Override
    public Role createRole(String roleName) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Role r = em.find(Role.class, roleName);
            if (r == null) {
                r = new Role(roleName);
                em.persist(r);
            }
            em.getTransaction().commit();
            return r;
        }
    }

    @Override
    public User addUserRole(String username, String roleName) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            User u = em.find(User.class, username);
            if (u == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("User not found");
            }
            Role r = em.find(Role.class, roleName);
            if (r == null) {
                r = new Role(roleName);
                em.persist(r);
            }
            u.addRole(r);
            User merged = em.merge(u);
            em.getTransaction().commit();
            return merged;
        }
    }
}
