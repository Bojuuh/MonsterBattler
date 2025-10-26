package app.controllers.impl;

import app.config.HibernateConfig;
import app.dtos.MonsterDTO;
import app.entities.Monster;
import app.daos.impl.MonsterDAO;
import app.controllers.IController;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class MonsterController implements IController<Monster, Integer> {

    private final MonsterDAO dao;

    public MonsterController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = MonsterDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Monster m = dao.read(id);
        ctx.status(m != null ? 200 : 404);
        ctx.json(m != null ? new MonsterDTO(m) : "{ \"status\": 404, \"msg\": \"Not found\" }");
    }

    @Override
    public void readAll(Context ctx) {
        List<Monster> list = dao.readAll();
        List<MonsterDTO> dtoList = list.stream().map(MonsterDTO::new).collect(Collectors.toList());
        ctx.status(200);
        ctx.json(dtoList);
    }

    @Override
    public void create(Context ctx) {
        Monster incoming = ctx.bodyAsClass(Monster.class);
        Monster created = dao.create(incoming);
        ctx.status(201);
        ctx.json(new MonsterDTO(created));
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Monster incoming = ctx.bodyAsClass(Monster.class);
        Monster updated = dao.update(id, incoming);
        ctx.status(updated != null ? 200 : 404);
        ctx.json(updated != null ? new MonsterDTO(updated) : "{ \"status\": 404, \"msg\": \"Not found\" }");
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        dao.delete(id);
        ctx.status(200);
        ctx.json("{ \"msg\": \"Monster deleted\" }");
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return dao.validatePrimaryKey(id);
    }

    @Override
    public Monster validateEntity(Context ctx) {
        return ctx.bodyAsClass(Monster.class);
    }

    public void populate(Context ctx) {
        dao.populate();
        ctx.res().setStatus(200);
        ctx.json("{ \"message\": \"Database has been populated\" }");
    }
}
