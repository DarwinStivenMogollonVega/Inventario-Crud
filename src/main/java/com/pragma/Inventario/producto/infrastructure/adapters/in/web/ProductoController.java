package com.pragma.Inventario.producto.infrastructure.adapters.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;

import com.pragma.Inventario.producto.application.ports.in.ProductoUseCase;
import com.pragma.Inventario.producto.infrastructure.adapters.mapper.ProductoMapper;
import com.pragma.Inventario.producto.infrastructure.adapters.in.web.form.ProductoForm;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/productos")
public class ProductoController {

	private final ProductoUseCase productoUseCase;
	private final ProductoMapper productoMapper;

	public ProductoController(ProductoUseCase productoUseCase, ProductoMapper productoMapper) {
		this.productoUseCase = productoUseCase;
		this.productoMapper = productoMapper;
	}

	@GetMapping
    public String listProducts(Model model) {
		model.addAttribute("productos", productoUseCase.findAllOrderedByName());
		return "productos/lista";
	}

	@GetMapping("/nuevo")
	@PreAuthorize("hasRole('ADMIN')")
	public String showCreateForm(Model model) {
		model.addAttribute("producto", new ProductoForm());
		model.addAttribute("titulo", "Nuevo producto");
		return "productos/formulario";
	}

	@GetMapping("/{id}/editar")
	@PreAuthorize("hasRole('ADMIN')")
	public String showEditForm(@PathVariable Long id, Model model) {
		model.addAttribute("producto", productoMapper.toForm(productoUseCase.findRequiredById(id)));
		model.addAttribute("titulo", "Editar producto");
		return "productos/formulario";
	}

	@PostMapping("/guardar")
	@PreAuthorize("hasRole('ADMIN')")
	public String saveProduct(
			@Valid @ModelAttribute("producto") ProductoForm productoForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("titulo", productoForm.getId() == null ? "Nuevo producto" : "Editar producto");
			return "productos/formulario";
		}

		productoUseCase.saveProduct(productoMapper.toDomain(productoForm));
		redirectAttributes.addFlashAttribute("mensaje", "Producto guardado correctamente");
		return "redirect:/productos";
	}

	@PostMapping("/{id}/eliminar")
	@PreAuthorize("hasRole('ADMIN')")
	public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		productoUseCase.deleteProductById(id);
		redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado correctamente");
		return "redirect:/productos";
	}
}