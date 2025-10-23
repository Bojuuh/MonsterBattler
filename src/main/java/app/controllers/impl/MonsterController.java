package app.controllers.impl;

import app.config.HibernateConfig;
import app.dtos.MonsterDTO;
import app.entities.Monster;
import app.daos.impl.MonsterDAO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MonsterController {

    private final MonsterDAO dao;

    public MonsterController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = MonsterDAO.getInstance(emf);
    }

    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Monster m = dao.read(id);
        ctx.status(m != null ? 200 : 404);
        ctx.json(m != null ? new MonsterDTO(m) : "{ \"status\": 404, \"msg\": \"Not found\" }");
    }

    public void readAll(Context ctx) {
        List<MonsterDTO> list = dao.readAll();
        ctx.status(200);
        ctx.json(list);
    }

    public void create(Context ctx) {
        Monster incoming = ctx.bodyAsClass(Monster.class);
        Monster created = dao.create(incoming);
        ctx.status(201);
        ctx.json(new MonsterDTO(created));
    }

    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Monster incoming = ctx.bodyAsClass(Monster.class);
        Monster updated = dao.update(id, incoming);
        ctx.status(200);
        ctx.json(new MonsterDTO(updated));
    }

    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        dao.delete(id);
        ctx.status(200);
        ctx.json("{ \"msg\": \"Monster deleted\" }");
    }
}

