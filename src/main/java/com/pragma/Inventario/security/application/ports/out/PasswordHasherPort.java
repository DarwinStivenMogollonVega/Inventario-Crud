package com.pragma.Inventario.security.application.ports.out;

public interface PasswordHasherPort {

    String hash(String rawPassword);
}
