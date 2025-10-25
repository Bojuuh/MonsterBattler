package app.security.controllers;

import app.security.dtos.UserDTO;
import app.security.enums.Role;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;

import java.util.Set;

public class AccessController {

    private final SecurityController securityController = SecurityController.getInstance();

    public void accessHandler(Context ctx) {
        Set<RouteRole> routeRoles = ctx.routeRoles();
        // open route if no roles or ANYONE allowed
        if (routeRoles == null || routeRoles.isEmpty() || routeRoles.contains(Role.ANYONE)) {
            return;
        }

        String authHeader = ctx.header("Authorization");
        UserDTO user = securityController.authenticate(authHeader);
        if (user == null) {
            throw new UnauthorizedResponse("Missing or invalid token");
        }
        ctx.attribute("user", user);

        if (!securityController.authorize(user, routeRoles)) {
            throw new UnauthorizedResponse("Not authorized for this endpoint");
        }
    }
}
