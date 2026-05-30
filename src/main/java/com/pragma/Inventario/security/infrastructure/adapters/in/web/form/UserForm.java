package com.pragma.Inventario.security.infrastructure.adapters.in.web.form;

import com.pragma.Inventario.security.domain.model.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserForm {

    private Long id;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 100, message = "El usuario no puede superar 100 caracteres")
    private String username;

    private String password;

    @Pattern(regexp = "^$|(USER|ADMIN)$", message = "El rol debe ser admin o user")
    private String role = "USER";

    public static UserForm from(User user) {
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setPassword("");
        form.setRole(toShortRole(user.getRole()));
        return form;
    }

    public User toDomain() {
        return new User(id, username, password, resolveRole());
    }

    private String resolveRole() {
        String normalizedRole = role == null || role.isBlank() ? "USER" : role;
        return "ROLE_" + normalizedRole.toUpperCase();
    }

    private static String toShortRole(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }
        return role.replace("ROLE_", "").toUpperCase();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}