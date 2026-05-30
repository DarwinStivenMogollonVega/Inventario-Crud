package com.pragma.Inventario.producto.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductoRepository extends JpaRepository<ProductoEntity, Long> {
}
