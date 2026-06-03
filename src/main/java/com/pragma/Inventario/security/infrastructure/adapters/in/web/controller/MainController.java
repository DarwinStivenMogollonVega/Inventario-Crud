package com.pragma.Inventario.security.infrastructure.adapters.in.web.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.pragma.Inventario.producto.application.ports.in.ProductoUseCase;
import com.pragma.Inventario.producto.domain.model.Producto;

@Controller
public class MainController {

    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final int RECENT_PRODUCTS_LIMIT = 5;

    private final ProductoUseCase productoUseCase;

    public MainController(ProductoUseCase productoUseCase) {
    this.productoUseCase = productoUseCase;
    }

    @GetMapping("/main")
    public String redirectToDashboard(Authentication authentication, Model model) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);

    List<Producto> productos = productoUseCase.findAllOrderedByName();
    List<Producto> productosRecientes = productos.stream()
        .sorted(Comparator.comparing(Producto::getId, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
        .limit(RECENT_PRODUCTS_LIMIT)
        .collect(Collectors.toList());
    List<Producto> productosBajoStock = productos.stream()
        .filter(producto -> producto.getCantidad() != null && producto.getCantidad() <= LOW_STOCK_THRESHOLD)
        .collect(Collectors.toList());

    model.addAttribute("totalProductos", productos.size());
    model.addAttribute("totalCategorias", productos.stream()
        .map(Producto::getCategoria)
        .filter(categoria -> categoria != null && !categoria.isBlank())
        .distinct()
        .count());
    model.addAttribute("productosBajoStock", productosBajoStock);
    model.addAttribute("totalProductosBajoStock", productosBajoStock.size());
    model.addAttribute("productosRecientes", productosRecientes);
    model.addAttribute("lowStockThreshold", LOW_STOCK_THRESHOLD);
        return "main";
    }
}