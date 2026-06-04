package com.pragma.Inventario.producto.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pragma.Inventario.infrastructure.adapters.in.web.ResourceNotFoundException;
import com.pragma.Inventario.producto.application.ports.out.ProductoRepositoryPort;
import com.pragma.Inventario.producto.domain.model.Producto;

@ExtendWith(MockitoExtension.class)
class ProductoApplicationServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @InjectMocks
    private ProductoApplicationService productoApplicationService;

    @Test
    void findAllOrderedByNameShouldReturnRepositoryResults() {
        List<Producto> productos = List.of(
                new Producto(1L, "Monitor", "Monitor 24", 3, new BigDecimal("900.00"), "TECNOLOGIA"),
                new Producto(2L, "Teclado", "Teclado mecanico", 5, new BigDecimal("120.00"), "TECNOLOGIA"));

        when(productoRepositoryPort.findAllOrderedByName()).thenReturn(productos);

        List<Producto> result = productoApplicationService.findAllOrderedByName();

        assertEquals(productos, result);
        verify(productoRepositoryPort).findAllOrderedByName();
    }

    @Test
    void findRequiredByIdShouldReturnProductWhenItExists() {
        Producto producto = new Producto(1L, "Mouse", "Mouse optico", 10, new BigDecimal("45.00"), "TECNOLOGIA");
        when(productoRepositoryPort.findById(1L)).thenReturn(Optional.of(producto));

        Producto result = productoApplicationService.findRequiredById(1L);

        assertEquals(producto, result);
        verify(productoRepositoryPort).findById(1L);
    }

    @Test
    void findRequiredByIdShouldThrowWhenProductDoesNotExist() {
        when(productoRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productoApplicationService.findRequiredById(99L));

        assertEquals("No se encontro el producto con id 99", exception.getMessage());
        verify(productoRepositoryPort).findById(99L);
    }

    @Test
    void saveProductShouldDelegateToRepository() {
        Producto producto = new Producto("Mesa", "Mesa de oficina", 2, new BigDecimal("250.00"), "HOGAR");
        Producto saved = new Producto(7L, "Mesa", "Mesa de oficina", 2, new BigDecimal("250.00"), "HOGAR");
        when(productoRepositoryPort.save(producto)).thenReturn(saved);

        Producto result = productoApplicationService.saveProduct(producto);

        assertEquals(saved, result);
        verify(productoRepositoryPort).save(producto);
    }

    @Test
    void deleteProductByIdShouldDeleteExistingProduct() {
        Producto producto = new Producto(5L, "Silla", "Silla ergonomica", 4, new BigDecimal("180.00"), "HOGAR");
        when(productoRepositoryPort.findById(5L)).thenReturn(Optional.of(producto));

        productoApplicationService.deleteProductById(5L);

        verify(productoRepositoryPort).findById(5L);
        verify(productoRepositoryPort).delete(producto);
    }

    @Test
    void deleteProductByIdShouldThrowWhenProductDoesNotExist() {
        when(productoRepositoryPort.findById(12L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoApplicationService.deleteProductById(12L));

        verify(productoRepositoryPort).findById(12L);
        verify(productoRepositoryPort, never()).delete(any(Producto.class));
    }
}