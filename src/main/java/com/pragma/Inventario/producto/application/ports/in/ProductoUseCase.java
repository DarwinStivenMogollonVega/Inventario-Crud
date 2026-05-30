package com.pragma.Inventario.producto.application.ports.in;

import java.util.List;

import com.pragma.Inventario.producto.domain.model.Producto;

public interface ProductoUseCase {

    List<Producto> findAllOrderedByName();

    Producto findRequiredById(Long id);

    Producto saveProduct(Producto producto);

    void deleteProductById(Long id);
}
