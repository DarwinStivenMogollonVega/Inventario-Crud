package com.pragma.Inventario.security.infrastructure.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.pragma.Inventario.security.application.ports.out.UserRepositoryPort;
import com.pragma.Inventario.security.domain.model.User;

@Component
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;

    public JpaUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public List<User> findAll() {
        return springDataUserRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataUserRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataUserRepository.findByNombre(username).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataUserRepository.existsByNombre(username);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return springDataUserRepository.existsByNombreAndIdNot(username, id);
    }

    @Override
    public User save(User user) {
        UserEntity savedEntity = springDataUserRepository.save(toEntity(user));
        return toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        springDataUserRepository.deleteById(id);
    }

    private User toDomain(UserEntity entity) {
        return new User(entity.getId(), entity.getNombre(), entity.getContrasena(), entity.getRol());
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setNombre(user.getUsername());
        entity.setContrasena(user.getPassword());
        entity.setRol(user.getRole());
        return entity;
    }
}
