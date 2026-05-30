package com.pragma.Inventario.security.infrastructure.adapters.in.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Usuario o contraseña inválidos";
    private static final String LOGOUT_MESSAGE = "Has cerrado sesión correctamente";

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", INVALID_CREDENTIALS_MESSAGE);
        }
        if (logout != null) {
            model.addAttribute("message", LOGOUT_MESSAGE);
        }
        return "login";
    }
}