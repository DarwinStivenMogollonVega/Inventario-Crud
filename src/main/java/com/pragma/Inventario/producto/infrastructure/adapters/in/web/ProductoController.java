package com.pragma.Inventario.producto.infrastructure.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pragma.Inventario.producto.application.ports.in.ProductoUseCase;
import com.pragma.Inventario.producto.infrastructure.adapters.in.web.form.ProductoForm;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/productos")
public class ProductoController {

	private final ProductoUseCase productoUseCase;

	public ProductoController(ProductoUseCase productoUseCase) {
		this.productoUseCase = productoUseCase;
	}

	@GetMapping
	    public String listProducts(
		    @RequestParam(required = false) String q,
		    @RequestParam(required = false) String categoria,
		    @RequestParam(required = false) BigDecimal minPrice,
		    @RequestParam(required = false) BigDecimal maxPrice,
		    @RequestParam(required = false, defaultValue = "false") boolean lowStock,
		    @RequestParam(required = false, defaultValue = "name") String sort,
		    @RequestParam(required = false, defaultValue = "1") Integer page,
		    Model model) {

		final int LOW_STOCK_THRESHOLD = 5;

		List<com.pragma.Inventario.producto.domain.model.Producto> productos = productoUseCase.findAllOrderedByName();

		// distinct categories for filter dropdown
		List<String> categorias = productos.stream()
				.map(com.pragma.Inventario.producto.domain.model.Producto::getCategoria)
				.filter(c -> c != null && !c.isBlank())
				.distinct()
				.collect(Collectors.toList());

		// apply filters
		java.util.stream.Stream<com.pragma.Inventario.producto.domain.model.Producto> stream = productos.stream();

		if (q != null && !q.isBlank()) {
			String qq = q.toLowerCase();
			stream = stream.filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(qq))
					|| (p.getCategoria() != null && p.getCategoria().toLowerCase().contains(qq))
					|| (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(qq)));
		}

		if (categoria != null && !categoria.isBlank()) {
			stream = stream.filter(p -> p.getCategoria() != null && p.getCategoria().equals(categoria));
		}

		if (minPrice != null) {
			stream = stream.filter(p -> p.getPrecio() != null && p.getPrecio().compareTo(minPrice) >= 0);
		}

		if (maxPrice != null) {
			stream = stream.filter(p -> p.getPrecio() != null && p.getPrecio().compareTo(maxPrice) <= 0);
		}

		if (lowStock) {
			stream = stream.filter(p -> p.getCantidad() != null && p.getCantidad() <= LOW_STOCK_THRESHOLD);
		}

		// sorting
		Comparator<com.pragma.Inventario.producto.domain.model.Producto> comparator = Comparator.comparing(
				com.pragma.Inventario.producto.domain.model.Producto::getNombre,
				Comparator.nullsLast(String::compareToIgnoreCase));

		if ("price".equalsIgnoreCase(sort) || "precio".equalsIgnoreCase(sort)) {
			comparator = Comparator.comparing(com.pragma.Inventario.producto.domain.model.Producto::getPrecio, Comparator.nullsLast(Comparator.naturalOrder()));
		} else if ("stock".equalsIgnoreCase(sort) || "cantidad".equalsIgnoreCase(sort)) {
			comparator = Comparator.comparing(com.pragma.Inventario.producto.domain.model.Producto::getCantidad, Comparator.nullsLast(Comparator.naturalOrder()));
		}

		List<com.pragma.Inventario.producto.domain.model.Producto> resultados = stream
			.sorted(comparator)
			.collect(Collectors.toList());

		// pagination
		int pageSize = 10; // default
		int pageNum = page != null ? page : 1; // 1-based

		int totalItems = resultados.size();
		int totalPages = (int) Math.max(1, Math.ceil((double) totalItems / pageSize));
		if (pageNum < 1) pageNum = 1;
		if (pageNum > totalPages) pageNum = totalPages;

		int fromIndex = (pageNum - 1) * pageSize;
		int toIndex = Math.min(fromIndex + pageSize, totalItems);
		List<com.pragma.Inventario.producto.domain.model.Producto> pageItems = resultados.subList(fromIndex, toIndex);

		model.addAttribute("productos", pageItems);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("categorias", categorias);
		model.addAttribute("q", q);
		model.addAttribute("selectedCategoria", categoria);
		model.addAttribute("minPrice", minPrice);
		model.addAttribute("maxPrice", maxPrice);
		model.addAttribute("lowStock", lowStock);
		model.addAttribute("sort", sort);

		return "productos/lista";
	}

	@GetMapping("/nuevo")
	public String showCreateForm(Model model) {
		model.addAttribute("producto", new ProductoForm());
		model.addAttribute("titulo", "Nuevo producto");
		return "productos/formulario";
	}

	@GetMapping("/{id}/editar")
	public String showEditForm(@PathVariable Long id, Model model) {
		try {
			model.addAttribute("producto", ProductoForm.from(productoUseCase.findRequiredById(id)));
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
		model.addAttribute("titulo", "Editar producto");
		return "productos/formulario";
	}

	@PostMapping("/guardar")
	public String saveProduct(
			@Valid @ModelAttribute("producto") ProductoForm productoForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("titulo", productoForm.getId() == null ? "Nuevo producto" : "Editar producto");
			return "productos/formulario";
		}

		productoUseCase.saveProduct(productoForm.toDomain());
		redirectAttributes.addFlashAttribute("mensaje", "Producto guardado correctamente");
		return "redirect:/productos";
	}

	@PostMapping("/{id}/eliminar")
	public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			productoUseCase.deleteProductById(id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
		redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado correctamente");
		return "redirect:/productos";
	}
}