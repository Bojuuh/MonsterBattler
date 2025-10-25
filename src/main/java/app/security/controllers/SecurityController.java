package app.security.controllers;

import app.config.HibernateConfig;
import app.security.daos.ISecurityDAO;
import app.security.daos.SecurityDAO;
import app.security.dtos.UserDTO;
import app.security.entities.User;
import app.security.exceptions.ValidationException;
import app.security.utils.TokenUtil;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController {

    private static SecurityController instance;
    private final ISecurityDAO dao;

    private SecurityController() {
        var emf = HibernateConfig.getEntityManagerFactory();
        this.dao = SecurityDAO.getInstance(emf);
    }

    public static SecurityController getInstance() {
        if (instance == null) instance = new SecurityController();
        return instance;
    }

    public void register(Context ctx) {
        var map = ctx.bodyAsClass(RegisterRequest.class);
        User u = dao.createUser(map.username, map.password);
        UserDTO dto = new UserDTO(u.getUsername(), u.getRolesAsStrings());
        String token = TokenUtil.createToken(dto);
        ctx.status(201);
        ctx.json(Map.of("token", token, "username", dto.getUsername(), "roles", dto.getRoles()));
    }

    public void login(Context ctx) {
        var map = ctx.bodyAsClass(LoginRequest.class);
        try {
            User u = dao.getVerifiedUser(map.username, map.password);
            UserDTO dto = new UserDTO(u.getUsername(), u.getRolesAsStrings());
            String token = TokenUtil.createToken(dto);
            ctx.status(200);
            ctx.json(Map.of("token", token, "username", dto.getUsername(), "roles", dto.getRoles()));
        } catch (ValidationException e) {
            throw new UnauthorizedResponse("Invalid credentials");
        }
    }

    public UserDTO authenticate(String bearerToken) {
        if (bearerToken == null) return null;
        String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;
        return TokenUtil.verifyToken(token);
    }

    public boolean authorize(UserDTO user, Set<RouteRole> allowedRoles) {
        if (user == null) return false;
        if (allowedRoles == null || allowedRoles.isEmpty()) return true;
        return allowedRoles.stream().anyMatch(r -> user.getRoles().contains(r.toString()));
    }

    // request helpers
    public static class RegisterRequest {
        public String username;
        public String password;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }
}
