package com.pragma.Inventario.security.infrastructure.adapters.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.nombre = :nombre AND u.id <> :id")
    boolean existsByNombreAndIdNot(@Param("nombre") String nombre, @Param("id") Long id);
}
