package app.routes;

import app.controllers.impl.MonsterController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MonsterRoute {
    private final MonsterController controller = new MonsterController();

    protected EndpointGroup getRoutes() {
        return () -> {
            get("/populate", controller::populate);
            get("/", controller::readAll);
            post("/", controller::create);
            get("/{id}", controller::read);
            put("/{id}", controller::update);
            delete("/{id}", controller::delete);
        };
    }
}

