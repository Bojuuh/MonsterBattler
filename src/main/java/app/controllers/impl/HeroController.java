package app.controllers.impl;

import app.config.HibernateConfig;
import app.dtos.HeroDTO;
import app.entities.Hero;
import app.daos.impl.HeroDAO;
import app.controllers.IController;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class HeroController implements IController<Hero, Integer> {

    private final HeroDAO dao;

    public HeroController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = HeroDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Hero hero = dao.read(id);
        ctx.status(hero != null ? 200 : 404);
        ctx.json(hero != null ? new HeroDTO(hero) : "{ \"status\": 404, \"msg\": \"Not found\" }");
    }

    @Override
    public void readAll(Context ctx) {
        List<Hero> list = dao.readAll();
        List<HeroDTO> dtoList = list.stream().map(HeroDTO::new).collect(Collectors.toList());
        ctx.status(200);
        ctx.json(dtoList);
    }

    @Override
    public void create(Context ctx) {
        Hero incoming = ctx.bodyAsClass(Hero.class);
        Hero created = dao.create(incoming);
        ctx.status(201);
        ctx.json(new HeroDTO(created));
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Hero incoming = ctx.bodyAsClass(Hero.class);
        Hero updated = dao.update(id, incoming);
        ctx.status(updated != null ? 200 : 404);
        ctx.json(updated != null ? new HeroDTO(updated) : "{ \"status\": 404, \"msg\": \"Not found\" }");
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        dao.delete(id);
        ctx.status(200);
        ctx.json("{ \"msg\": \"Hero deleted\" }");
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return dao.validatePrimaryKey(id);
    }

    @Override
    public Hero validateEntity(Context ctx) {
        return ctx.bodyAsClass(Hero.class);
    }
}
