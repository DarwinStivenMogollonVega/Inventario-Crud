package com.pragma.Inventario.security.infrastructure.adapters.in.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pragma.Inventario.security.application.ports.in.UserManagementUseCase;
import com.pragma.Inventario.security.infrastructure.adapters.in.rest.dto.request.CreateUserRequest;
import com.pragma.Inventario.security.infrastructure.adapters.in.rest.dto.request.UpdateUserRequest;
import com.pragma.Inventario.security.infrastructure.adapters.in.rest.dto.response.UserResponse;
import com.pragma.Inventario.security.infrastructure.adapters.mapper.UserMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserManagementUseCase userManagementUseCase;
    private final UserMapper userMapper;

    public UserRestController(UserManagementUseCase userManagementUseCase, UserMapper userMapper) {
        this.userManagementUseCase = userManagementUseCase;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return userManagementUseCase.findAllUsers().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userMapper.toResponse(userManagementUseCase.findRequiredById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        var mapped = userMapper.toDomain(request);
        var creado = userManagementUseCase.registerUser(mapped.getUsername(), mapped.getPassword(), mapped.getRole());
        return ResponseEntity.created(URI.create("/api/users/" + creado.getId())).body(userMapper.toResponse(creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        String rawPassword = hasText(request.getPassword()) ? request.getPassword() : null;
        var mapped = userMapper.toDomain(request);
        var actualizado = userManagementUseCase.updateUserDetails(id, mapped.getUsername(), rawPassword, mapped.getRole());
        return ResponseEntity.ok(userMapper.toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userManagementUseCase.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
