package com.pragma.Inventario.security.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pragma.Inventario.security.application.exception.UserAlreadyExistsException;
import com.pragma.Inventario.security.application.exception.UserNotFoundException;
import com.pragma.Inventario.security.application.ports.out.PasswordHasherPort;
import com.pragma.Inventario.security.application.ports.out.UserRepositoryPort;
import com.pragma.Inventario.security.domain.model.User;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordHasherPort passwordHasherPort;

    @InjectMocks
    private UserManagementService userManagementService;

    @Test
    void findAllUsersShouldReturnRepositoryResults() {
        List<User> users = List.of(
                new User(1L, "admin", "hash-1", "ROLE_ADMIN"),
                new User(2L, "user", "hash-2", "ROLE_USER"));

        when(userRepositoryPort.findAll()).thenReturn(users);

        List<User> result = userManagementService.findAllUsers();

        assertEquals(users, result);
        verify(userRepositoryPort).findAll();
    }

    @Test
    void findRequiredByIdShouldReturnUserWhenItExists() {
        User user = new User(1L, "admin", "hash", "ROLE_ADMIN");
        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));

        User result = userManagementService.findRequiredById(1L);

        assertEquals(user, result);
        verify(userRepositoryPort).findById(1L);
    }

    @Test
    void findRequiredByIdShouldThrowWhenUserDoesNotExist() {
        when(userRepositoryPort.findById(40L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userManagementService.findRequiredById(40L));

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepositoryPort).findById(40L);
    }

    @Test
    void findRequiredByUsernameShouldReturnUserWhenItExists() {
        User user = new User(3L, "jane", "hash", "ROLE_USER");
        when(userRepositoryPort.findByUsername("jane")).thenReturn(Optional.of(user));

        User result = userManagementService.findRequiredByUsername("jane");

        assertEquals(user, result);
        verify(userRepositoryPort).findByUsername("jane");
    }

    @Test
    void registerUserShouldHashPasswordAndNormalizeRole() {
        when(userRepositoryPort.existsByUsername("newuser")).thenReturn(false);
        when(passwordHasherPort.hash("secret")).thenReturn("hashed-secret");
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userManagementService.registerUser("newuser", "secret", "admin");

        assertEquals("newuser", result.getUsername());
        assertEquals("hashed-secret", result.getPassword());
        assertEquals("ROLE_ADMIN", result.getRole());
        verify(passwordHasherPort).hash("secret");
        verify(userRepositoryPort).existsByUsername("newuser");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void registerUserShouldThrowWhenUsernameAlreadyExists() {
        when(userRepositoryPort.existsByUsername("duplicate")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userManagementService.registerUser("duplicate", "secret", "USER"));

        assertEquals("El usuario ya existe", exception.getMessage());
        verify(userRepositoryPort).existsByUsername("duplicate");
        verify(passwordHasherPort, never()).hash(any());
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    void updateUserDetailsShouldKeepCurrentPasswordWhenNewPasswordIsBlank() {
        User existingUser = new User(10L, "oldname", "stored-hash", "ROLE_USER");
        when(userRepositoryPort.findById(10L)).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.existsByUsernameAndIdNot("oldname", 10L)).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userManagementService.updateUserDetails(10L, "oldname", "", "user");

        assertEquals(10L, result.getId());
        assertEquals("oldname", result.getUsername());
        assertEquals("stored-hash", result.getPassword());
        assertEquals("ROLE_USER", result.getRole());
        verify(passwordHasherPort, never()).hash(any());
        verify(userRepositoryPort).existsByUsernameAndIdNot("oldname", 10L);
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void updateUserDetailsShouldHashNewPasswordWhenProvided() {
        User existingUser = new User(11L, "current", "stored-hash", "ROLE_USER");
        when(userRepositoryPort.findById(11L)).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.existsByUsernameAndIdNot("current", 11L)).thenReturn(false);
        when(passwordHasherPort.hash("new-secret")).thenReturn("new-hash");
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userManagementService.updateUserDetails(11L, "current", "new-secret", "admin");

        assertEquals("new-hash", result.getPassword());
        assertEquals("ROLE_ADMIN", result.getRole());
        verify(passwordHasherPort).hash("new-secret");
    }

    @Test
    void updateUserDetailsShouldThrowWhenUsernameAlreadyExistsForAnotherUser() {
        User existingUser = new User(12L, "current", "stored-hash", "ROLE_USER");
        when(userRepositoryPort.findById(12L)).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.existsByUsernameAndIdNot("taken", 12L)).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userManagementService.updateUserDetails(12L, "taken", null, "USER"));

        assertEquals("El usuario ya existe", exception.getMessage());
        verify(userRepositoryPort).findById(12L);
        verify(userRepositoryPort).existsByUsernameAndIdNot("taken", 12L);
        verify(passwordHasherPort, never()).hash(any());
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    void deleteUserByIdShouldValidateExistenceBeforeDeleting() {
        User existingUser = new User(15L, "delete-me", "hash", "ROLE_USER");
        when(userRepositoryPort.findById(15L)).thenReturn(Optional.of(existingUser));

        userManagementService.deleteUserById(15L);

        verify(userRepositoryPort).findById(15L);
        verify(userRepositoryPort).deleteById(15L);
    }

    @Test
    void deleteUserByIdShouldThrowWhenUserDoesNotExist() {
        when(userRepositoryPort.findById(16L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userManagementService.deleteUserById(16L));

        verify(userRepositoryPort).findById(16L);
        verify(userRepositoryPort, never()).deleteById(16L);
    }

    @Test
    void seedDefaultUserIfMissingShouldCreateUserOnlyWhenAbsent() {
        when(userRepositoryPort.existsByUsername("seed")).thenReturn(false);
        when(passwordHasherPort.hash("secret")).thenReturn("hashed-secret");
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userManagementService.seedDefaultUserIfMissing("seed", "secret", "user");

        verify(userRepositoryPort).existsByUsername("seed");
        verify(passwordHasherPort).hash("secret");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void seedDefaultUserIfMissingShouldDoNothingWhenUserAlreadyExists() {
        when(userRepositoryPort.existsByUsername("seed")).thenReturn(true);

        userManagementService.seedDefaultUserIfMissing("seed", "secret", "user");

        verify(userRepositoryPort).existsByUsername("seed");
        verifyNoInteractions(passwordHasherPort);
        verify(userRepositoryPort, never()).save(any(User.class));
    }
}