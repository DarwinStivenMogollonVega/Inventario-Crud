package com.pragma.Inventario.producto.infrastructure.adapters.in.web;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.pragma.Inventario.producto.application.ports.in.ProductoUseCase;
import com.pragma.Inventario.producto.domain.model.Producto;

@Controller
public class CategoriaController {

    private final ProductoUseCase productoUseCase;

    public CategoriaController(ProductoUseCase productoUseCase) {
        this.productoUseCase = productoUseCase;
    }

    @GetMapping("/categorias")
    public String listCategories(Model model) {
        List<Producto> productos = productoUseCase.findAllOrderedByName();
        LinkedHashSet<String> categorias = productos.stream()
            .map(Producto::getCategoria)
            .filter(categoria -> categoria != null && !categoria.isBlank())
            .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));

        // counts per category preserving insertion order
        Map<String, Long> categoriasCounts = productos.stream()
            .filter(p -> p.getCategoria() != null && !p.getCategoria().isBlank())
            .collect(Collectors.groupingBy(Producto::getCategoria, LinkedHashMap::new, Collectors.counting()));

        model.addAttribute("categorias", categorias);
        model.addAttribute("categoriasCounts", categoriasCounts);
        model.addAttribute("totalCategorias", categorias.size());
        model.addAttribute("totalProductos", productos.size());
        return "categorias/lista";
    }
}