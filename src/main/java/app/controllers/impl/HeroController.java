package app.controllers.impl;

import app.config.HibernateConfig;
import app.dtos.HeroDTO;
import app.entities.Hero;
import app.daos.impl.HeroDAO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class HeroController {

    private final HeroDAO dao;

    public HeroController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = HeroDAO.getInstance(emf);
    }

    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Hero hero = dao.read(id);
        ctx.status(hero != null ? 200 : 404);
        ctx.json(hero != null ? new HeroDTO(hero) : "{ \"status\": 404, \"msg\": \"Not found\" }");
    }

    public void readAll(Context ctx) {
        List<HeroDTO> list = dao.readAll();
        ctx.status(200);
        ctx.json(list);
    }

    public void create(Context ctx) {
        Hero incoming = ctx.bodyAsClass(Hero.class);
        Hero created = dao.create(incoming);
        ctx.status(201);
        ctx.json(new HeroDTO(created));
    }

    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Hero incoming = ctx.bodyAsClass(Hero.class);
        Hero updated = dao.update(id, incoming);
        ctx.status(200);
        ctx.json(new HeroDTO(updated));
    }

    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        dao.delete(id);
        ctx.status(200);
        ctx.json("{ \"msg\": \"Hero deleted\" }");
    }
}
