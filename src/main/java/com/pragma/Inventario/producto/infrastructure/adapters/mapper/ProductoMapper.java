package com.pragma.Inventario.producto.infrastructure.adapters.mapper;

import org.springframework.stereotype.Component;

import com.pragma.Inventario.producto.domain.model.Producto;
import com.pragma.Inventario.producto.infrastructure.adapters.in.rest.dto.request.CreateProductRequest;
import com.pragma.Inventario.producto.infrastructure.adapters.in.rest.dto.request.UpdateProductRequest;
import com.pragma.Inventario.producto.infrastructure.adapters.in.rest.dto.response.ProductResponse;
import com.pragma.Inventario.producto.infrastructure.adapters.in.web.form.ProductoForm;

@Component
public class ProductoMapper {

    public Producto toDomain(CreateProductRequest request) {
        return new Producto(request.getNombre(), request.getDescripcion(), request.getCantidad(), request.getPrecio(), request.getCategoria());
    }

    public Producto toDomain(UpdateProductRequest request, Long id) {
        return new Producto(id, request.getNombre(), request.getDescripcion(), request.getCantidad(), request.getPrecio(), request.getCategoria());
    }

    public Producto toDomain(ProductoForm form) {
        return new Producto(form.getId(), form.getNombre(), form.getDescripcion(), form.getCantidad(), form.getPrecio(), form.getCategoria());
    }

    public ProductoForm toForm(Producto producto) {
        ProductoForm form = new ProductoForm();
        form.setId(producto.getId());
        form.setNombre(producto.getNombre());
        form.setDescripcion(producto.getDescripcion());
        form.setCantidad(producto.getCantidad());
        form.setPrecio(producto.getPrecio());
        form.setCategoria(producto.getCategoria());
        return form;
    }

    public ProductResponse toResponse(Producto producto) {
        ProductResponse response = new ProductResponse();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setCantidad(producto.getCantidad());
        response.setPrecio(producto.getPrecio());
        response.setCategoria(producto.getCategoria());
        return response;
    }
}