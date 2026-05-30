package com.pragma.Inventario.security.application.ports.out;

import java.util.List;
import java.util.Optional;

import com.pragma.Inventario.security.domain.model.User;

public interface UserRepositoryPort {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    User save(User user);

    void deleteById(Long id);
}
