package com.pragma.Inventario.producto.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pragma.Inventario.producto.application.ports.in.ProductoUseCase;
import com.pragma.Inventario.producto.application.ports.out.ProductoRepositoryPort;
import com.pragma.Inventario.producto.domain.model.Producto;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductoApplicationService implements ProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    public ProductoApplicationService(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    @Override
    public List<Producto> findAllOrderedByName() {
        return productoRepositoryPort.findAllOrderedByName();
    }

    @Override
    public Producto findRequiredById(Long id) {
        return productoRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el producto con id " + id));
    }

    @Override
    public Producto saveProduct(Producto producto) {
        return productoRepositoryPort.save(producto);
    }

    @Override
    public void deleteProductById(Long id) {
        productoRepositoryPort.delete(findRequiredById(id));
    }
}
