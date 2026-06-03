package com.pragma.Inventario.security.infrastructure.adapters.in.web;

import com.pragma.Inventario.security.application.ports.in.UserManagementUseCase;
import com.pragma.Inventario.security.domain.model.User;
import com.pragma.Inventario.security.infrastructure.adapters.in.web.form.UserForm;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserManagementUseCase userManagementUseCase;

    public AdminUserController(UserManagementUseCase userManagementUseCase) {
        this.userManagementUseCase = userManagementUseCase;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userManagementUseCase.findAllUsers());
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserForm());
        return "admin/users/form";
    }

    @PostMapping
    public String saveUser(@ModelAttribute("user") @Valid UserForm userForm, BindingResult result, RedirectAttributes redirectAttributes) {
        validateUsernameAvailability(userForm.getUsername(), null, result);
        validatePasswordPresence(userForm.getPassword(), result);

        if (result.hasErrors()) {
            return "admin/users/form";
        }

        userManagementUseCase.registerUser(userForm.getUsername(), userForm.getPassword(), userForm.getRole());
        redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = findRequiredUserById(id);
        model.addAttribute("user", UserForm.from(user));
        return "admin/users/form";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") @Valid UserForm userForm, BindingResult result, RedirectAttributes redirectAttributes) {
        validateUsernameAvailability(userForm.getUsername(), id, result);

        if (result.hasErrors()) {
            return "admin/users/form";
        }

        String rawPassword = hasText(userForm.getPassword()) ? userForm.getPassword() : null;
        userManagementUseCase.updateUserDetails(id, userForm.getUsername(), rawPassword, userForm.getRole());
        redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userManagementUseCase.deleteUserById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        return "redirect:/admin/users";
    }

    private void validateUsernameAvailability(String username, Long currentUserId, BindingResult result) {
        if (!hasText(username)) {
            return;
        }

        boolean usernameTaken = currentUserId == null
            ? userManagementUseCase.existsByUsername(username)
            : userManagementUseCase.existsByUsernameAndIdNot(username, currentUserId);

        if (usernameTaken) {
            result.rejectValue("username", "username.taken", "El usuario ya existe");
        }
    }

    private void validatePasswordPresence(String password, BindingResult result) {
        if (!hasText(password)) {
            result.rejectValue("password", "password.required", "La contraseña es obligatoria");
        }
    }

    private User findRequiredUserById(Long id) {
        try {
            return userManagementUseCase.findRequiredById(id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado", ex);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}