package app.routes;

import app.controllers.impl.HeroController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class HeroRoute {
    private final HeroController controller = new HeroController();

    protected EndpointGroup getRoutes() {
        return () -> {
            get("/", controller::readAll);
            post("/", controller::create);
            get("/{id}", controller::read);
            put("/{id}", controller::update);
            delete("/{id}", controller::delete);
        };
    }
}

