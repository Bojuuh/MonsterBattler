package app.security.routes;

import app.security.controllers.SecurityController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
//import io.javalin.apibuilder.Endpoint;

import static io.javalin.apibuilder.ApiBuilder.*;

public class SecurityRoutes {

    private static final SecurityController controller = SecurityController.getInstance();

    public static EndpointGroup getSecurityRoutes() {
        return () -> {
            path("/auth", () -> {
                post("/register", controller::register);
                post("/login", controller::login);
            });
        };
    }

    // demo secured endpoints
    public static EndpointGroup getSecuredRoutes() {
        return () -> {
            path("/protected", () -> {
                // user demo: any authenticated user with STANDARD role
                get("/user_demo", ctx -> ctx.json("{ \"msg\": \"User content\" }"), Role.STANDARD);
                // admin demo: only ADMIN
                get("/admin_demo", ctx -> ctx.json("{ \"msg\": \"Admin content\" }"), Role.ADMIN);
            });
        };
    }
}
