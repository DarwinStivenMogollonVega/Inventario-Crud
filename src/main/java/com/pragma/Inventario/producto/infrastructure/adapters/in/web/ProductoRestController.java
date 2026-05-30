package com.pragma.Inventario.producto.infrastructure.adapters.in.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pragma.Inventario.producto.application.ports.in.ProductoUseCase;
import com.pragma.Inventario.producto.infrastructure.adapters.in.rest.dto.request.CreateProductRequest;
import com.pragma.Inventario.producto.infrastructure.adapters.in.rest.dto.request.UpdateProductRequest;
import com.pragma.Inventario.producto.infrastructure.adapters.in.rest.dto.response.ProductResponse;
import com.pragma.Inventario.producto.infrastructure.adapters.mapper.ProductoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoUseCase productoUseCase;
    private final ProductoMapper productoMapper;

    public ProductoRestController(ProductoUseCase productoUseCase, ProductoMapper productoMapper) {
        this.productoUseCase = productoUseCase;
        this.productoMapper = productoMapper;
    }

    @GetMapping
    public List<ProductResponse> listProducts() {
        return productoUseCase.findAllOrderedByName().stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productoMapper.toResponse(productoUseCase.findRequiredById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        var creado = productoUseCase.saveProduct(productoMapper.toDomain(request));
        return ResponseEntity.created(URI.create("/api/productos/" + creado.getId())).body(productoMapper.toResponse(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        productoUseCase.findRequiredById(id);
        var actualizado = productoUseCase.saveProduct(productoMapper.toDomain(request, id));
        return ResponseEntity.ok(productoMapper.toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productoUseCase.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}