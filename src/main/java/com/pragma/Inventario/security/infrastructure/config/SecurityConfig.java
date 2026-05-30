package com.pragma.Inventario.security.infrastructure.config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/", "/productos").authenticated()
                .requestMatchers(HttpMethod.GET, "/main").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/admin/users", "/admin/users/new", "/admin/users/*/edit").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/admin/users", "/admin/users/*", "/admin/users/*/delete").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/productos/*/editar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/productos/nuevo").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/productos/guardar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/productos/*/eliminar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/productos").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/productos/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/productos/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/*").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(this::handleLoginSuccess)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            );

        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    private void handleLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        response.sendRedirect(isAdmin ? "/main" : "/productos");
    }
}