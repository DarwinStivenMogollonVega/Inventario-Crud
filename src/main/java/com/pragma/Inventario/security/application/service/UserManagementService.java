package com.pragma.Inventario.security.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pragma.Inventario.security.application.exception.UserAlreadyExistsException;
import com.pragma.Inventario.security.application.exception.UserNotFoundException;
import com.pragma.Inventario.security.application.ports.in.UserManagementUseCase;
import com.pragma.Inventario.security.application.ports.out.PasswordHasherPort;
import com.pragma.Inventario.security.application.ports.out.UserRepositoryPort;
import com.pragma.Inventario.security.domain.model.User;

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
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }

    @Override
    public User findRequiredByUsername(String username) {
        return userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
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
        assertUsernameAvailable(username, null);
        return createAndSaveUser(username, rawPassword, role, null);
    }

    @Override
    public User updateUserDetails(Long id, String username, String rawPassword, String role) {
        User existingUser = findRequiredById(id);
        assertUsernameAvailable(username, id);
        return createAndSaveUser(username, rawPassword, role, existingUser.getPassword(), id);
    }

    @Override
    public void deleteUserById(Long id) {
        findRequiredById(id);
        userRepositoryPort.deleteById(id);
    }

    @Override
    public void seedDefaultUserIfMissing(String username, String rawPassword, String role) {
        if (existsByUsername(username)) {
            return;
        }
        createAndSaveUser(username, rawPassword, role, null);
    }

    private User createAndSaveUser(String username, String rawPassword, String role, String existingPassword) {
        return createAndSaveUser(username, rawPassword, role, existingPassword, null);
    }

    private User createAndSaveUser(String username, String rawPassword, String role, String existingPassword, Long id) {
        String passwordToStore = rawPassword != null && !rawPassword.isBlank()
                ? passwordHasherPort.hash(rawPassword)
                : existingPassword;
        User user = new User(id, username, passwordToStore, normalizeRoleName(role));
        return userRepositoryPort.save(user);
    }

    private String normalizeRoleName(String role) {
        String resolvedRole = role == null || role.isBlank() ? "USER" : role;
        return resolvedRole.startsWith("ROLE_") ? resolvedRole : "ROLE_" + resolvedRole.toUpperCase();
    }

    private void assertUsernameAvailable(String username, Long currentUserId) {
        boolean usernameTaken = currentUserId == null
                ? existsByUsername(username)
                : existsByUsernameAndIdNot(username, currentUserId);

        if (usernameTaken) {
            throw new UserAlreadyExistsException("El usuario ya existe");
        }
    }
}
