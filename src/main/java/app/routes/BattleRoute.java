package app.routes;

import app.controllers.impl.BattleController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class BattleRoute {
    private final BattleController controller = new BattleController();

    protected EndpointGroup getRoutes() {
        return () -> {
            post("/start", controller::startBattle);
            get("/", controller::readAll);
            get("/{id}", controller::read);
        };
    }
}

