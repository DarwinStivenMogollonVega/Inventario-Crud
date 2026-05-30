package com.pragma.Inventario.security.infrastructure.adapters.mapper;

import org.springframework.stereotype.Component;

import com.pragma.Inventario.security.domain.model.User;
import com.pragma.Inventario.security.infrastructure.adapters.in.rest.dto.request.CreateUserRequest;
import com.pragma.Inventario.security.infrastructure.adapters.in.rest.dto.request.UpdateUserRequest;
import com.pragma.Inventario.security.infrastructure.adapters.in.rest.dto.response.UserResponse;
import com.pragma.Inventario.security.infrastructure.adapters.in.web.form.UserForm;

@Component
public class UserMapper {

    public User toDomain(CreateUserRequest request) {
        return new User(null, request.getUsername(), request.getPassword(), normalizeRole(request.getRole()));
    }

    public User toDomain(UpdateUserRequest request) {
        return new User(null, request.getUsername(), request.getPassword(), normalizeRole(request.getRole()));
    }

    public User toDomain(UserForm form) {
        return new User(form.getId(), form.getUsername(), form.getPassword(), normalizeRole(form.getRole()));
    }

    public UserForm toForm(User user) {
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setPassword("");
        form.setRole(shortRole(user.getRole()));
        return form;
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(shortRole(user.getRole()));
        return response;
    }

    private String normalizeRole(String role) {
        String resolvedRole = role == null || role.isBlank() ? "USER" : role;
        return resolvedRole.startsWith("ROLE_") ? resolvedRole : "ROLE_" + resolvedRole.toUpperCase();
    }

    private String shortRole(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }
        return role.replace("ROLE_", "").toUpperCase();
    }
}