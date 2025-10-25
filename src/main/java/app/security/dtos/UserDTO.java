package app.security.dtos;

import io.javalin.security.RouteRole;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserDTO implements Serializable {
    private String username;
    private Set<String> roles = new HashSet<>();

    public UserDTO() {}

    public UserDTO(String username, Set<String> roles) {
        this.username = username;
        if (roles != null) this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean hasRole(RouteRole role) {
        if (role == null) return false;
        return roles.contains(role.toString());
    }

    @Override
    public String toString() {
        return "UserDTO{username=" + username + ", roles=" + roles + "}";
    }
}
