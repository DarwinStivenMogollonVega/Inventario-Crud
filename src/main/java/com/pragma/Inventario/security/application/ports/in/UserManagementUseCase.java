package com.pragma.Inventario.security.application.ports.in;

import java.util.List;

import com.pragma.Inventario.security.domain.model.User;

public interface UserManagementUseCase {

    List<User> findAllUsers();

    User findRequiredById(Long id);

    User findRequiredByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    User registerUser(String username, String rawPassword, String role);

    User updateUserDetails(Long id, String username, String rawPassword, String role);

    void deleteUserById(Long id);

    void seedDefaultUserIfMissing(String username, String rawPassword, String role);
}
