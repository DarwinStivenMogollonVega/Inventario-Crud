package com.pragma.Inventario.producto.application.ports.out;

import java.util.List;
import java.util.Optional;

import com.pragma.Inventario.producto.domain.model.Producto;

public interface ProductoRepositoryPort {

    List<Producto> findAllOrderedByName();

    Optional<Producto> findById(Long id);

    Producto save(Producto producto);

    void delete(Producto producto);
}
