package com.pragma.Inventario.security.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pragma.Inventario.security.application.ports.in.UserManagementUseCase;
import com.pragma.Inventario.security.application.ports.out.PasswordHasherPort;
import com.pragma.Inventario.security.application.ports.out.UserRepositoryPort;
import com.pragma.Inventario.security.domain.model.User;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserManagementService implements UserManagementUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;

    public UserManagementService(UserRepositoryPort userRepositoryPort, PasswordHasherPort passwordHasherPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordHasherPort = passwordHasherPort;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepositoryPort.findAll();
    }

    @Override
    public User findRequiredById(Long id) {
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    @Override
    public User findRequiredByUsername(String username) {
        return userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepositoryPort.existsByUsername(username);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return userRepositoryPort.existsByUsernameAndIdNot(username, id);
    }

    @Override
    public User registerUser(String username, String rawPassword, String role) {
        User user = new User(username, passwordHasherPort.hash(rawPassword), normalizeRoleName(role));
        return userRepositoryPort.save(user);
    }

    @Override
    public User updateUserDetails(Long id, String username, String rawPassword, String role) {
        User existingUser = findRequiredById(id);
        String passwordToStore = rawPassword != null && !rawPassword.isBlank()
                ? passwordHasherPort.hash(rawPassword)
                : existingUser.getPassword();
        User updatedUser = new User(id, username, passwordToStore, normalizeRoleName(role));
        return userRepositoryPort.save(updatedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepositoryPort.deleteById(id);
    }

    @Override
    public void seedDefaultUserIfMissing(String username, String rawPassword, String role) {
        if (existsByUsername(username)) {
            return;
        }
        registerUser(username, rawPassword, role);
    }

    private String normalizeRoleName(String role) {
        String resolvedRole = role == null || role.isBlank() ? "USER" : role;
        return resolvedRole.startsWith("ROLE_") ? resolvedRole : "ROLE_" + resolvedRole.toUpperCase();
    }
}
