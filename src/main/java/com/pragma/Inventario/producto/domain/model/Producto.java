package com.pragma.Inventario.producto.domain.model;

import java.math.BigDecimal;

public class Producto {

    private final Long id;
    private final String nombre;
    private final String descripcion;
    private final Integer cantidad;
    private final BigDecimal precio;
    private final String categoria;

    public Producto(String nombre, String descripcion, Integer cantidad, BigDecimal precio, String categoria) {
        this(null, nombre, descripcion, cantidad, precio, categoria);
    }

    public Producto(Long id, String nombre, String descripcion, Integer cantidad, BigDecimal precio, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public String getCategoria() {
        return categoria;
    }
}
