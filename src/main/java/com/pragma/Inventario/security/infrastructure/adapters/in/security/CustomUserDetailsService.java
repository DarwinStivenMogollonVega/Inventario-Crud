package com.pragma.Inventario.security.infrastructure.adapters.in.security;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pragma.Inventario.security.application.ports.in.UserManagementUseCase;
import com.pragma.Inventario.security.domain.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserManagementUseCase userManagementUseCase;

    public CustomUserDetailsService(UserManagementUseCase userManagementUseCase) {
        this.userManagementUseCase = userManagementUseCase;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByUsername(username);
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), Collections.singleton(authority));
    }

    private User findUserByUsername(String username) {
        try {
            return userManagementUseCase.findRequiredByUsername(username);
        } catch (Exception exception) {
            throw new UsernameNotFoundException("User not found", exception);
        }
    }
}