package com.pragma.Inventario.security.infrastructure.adapters.in.web;

import com.pragma.Inventario.security.application.ports.in.UserManagementUseCase;
import com.pragma.Inventario.security.domain.model.User;
import com.pragma.Inventario.security.infrastructure.adapters.in.web.form.UserForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import com.pragma.Inventario.security.application.exception.UserAlreadyExistsException;
import com.pragma.Inventario.security.infrastructure.adapters.mapper.UserMapper;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserManagementUseCase userManagementUseCase;
    private final UserMapper userMapper;

    public AdminUserController(UserManagementUseCase userManagementUseCase, UserMapper userMapper) {
        this.userManagementUseCase = userManagementUseCase;
        this.userMapper = userMapper;
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
    public String saveUser(@ModelAttribute("user") @Valid UserForm userForm, BindingResult result) {
        validatePasswordPresence(userForm.getPassword(), result);

        if (result.hasErrors()) {
            return "admin/users/form";
        }

        try {
            User mapped = userMapper.toDomain(userForm);
            userManagementUseCase.registerUser(mapped.getUsername(), mapped.getPassword(), mapped.getRole());
        } catch (UserAlreadyExistsException ex) {
            result.rejectValue("username", "username.taken");
            return "admin/users/form";
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = findRequiredUserById(id);
        model.addAttribute("user", userMapper.toForm(user));
        return "admin/users/form";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") @Valid UserForm userForm, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/users/form";
        }

        String rawPassword = hasText(userForm.getPassword()) ? userForm.getPassword() : null;
        try {
            User mapped = userMapper.toDomain(userForm);
            userManagementUseCase.updateUserDetails(id, mapped.getUsername(), rawPassword, mapped.getRole());
        } catch (UserAlreadyExistsException ex) {
            result.rejectValue("username", "username.taken");
            return "admin/users/form";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userManagementUseCase.deleteUserById(id);
        return "redirect:/admin/users";
    }

    private void validatePasswordPresence(String password, BindingResult result) {
        if (!hasText(password)) {
            result.rejectValue("password", "password.required");
        }
    }

    private User findRequiredUserById(Long id) {
        return userManagementUseCase.findRequiredById(id);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}