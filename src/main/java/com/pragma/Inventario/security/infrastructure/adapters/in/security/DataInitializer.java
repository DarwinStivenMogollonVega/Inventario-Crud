package com.pragma.Inventario.security.infrastructure.adapters.in.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pragma.Inventario.security.application.ports.in.UserManagementUseCase;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedDefaultUsers(UserManagementUseCase userManagementUseCase) {
        return args -> {
            userManagementUseCase.seedDefaultUserIfMissing("administrador", "administrador", "ROLE_ADMIN");
            userManagementUseCase.seedDefaultUserIfMissing("usuario", "usuario", "ROLE_USER");
        };
    }
}