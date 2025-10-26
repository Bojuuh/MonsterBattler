package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final HeroRoute heroRoute = new HeroRoute();
    private final MonsterRoute monsterRoute = new MonsterRoute();
    private final BattleRoute battleRoute = new BattleRoute();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/heroes", heroRoute.getRoutes());
            path("/monsters", monsterRoute.getRoutes());
            path("/battles", battleRoute.getRoutes());

        };
    }
}
