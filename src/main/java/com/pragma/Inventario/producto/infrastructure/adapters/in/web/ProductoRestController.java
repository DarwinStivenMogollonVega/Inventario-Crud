package com.pragma.Inventario.producto.infrastructure.adapters.in.web;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
import com.pragma.Inventario.producto.domain.model.Producto;
import com.pragma.Inventario.producto.infrastructure.adapters.in.web.form.ProductoForm;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoUseCase productoUseCase;

    public ProductoRestController(ProductoUseCase productoUseCase) {
        this.productoUseCase = productoUseCase;
    }

    @GetMapping
    public List<ProductoForm> listProducts() {
        return productoUseCase.findAllOrderedByName().stream()
                .map(ProductoForm::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductoForm getProduct(@PathVariable Long id) {
        return ProductoForm.from(productoUseCase.findRequiredById(id));
    }

    @PostMapping
    public ResponseEntity<ProductoForm> createProduct(@Valid @RequestBody ProductoForm productoForm) {
        Producto creado = productoUseCase.saveProduct(productoForm.toDomain());
        return ResponseEntity.created(URI.create("/api/productos/" + creado.getId())).body(ProductoForm.from(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoForm> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductoForm productoForm) {
        productoUseCase.findRequiredById(id);
        productoForm.setId(id);
        Producto actualizado = productoUseCase.saveProduct(productoForm.toDomain());
        return ResponseEntity.ok(ProductoForm.from(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productoUseCase.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}