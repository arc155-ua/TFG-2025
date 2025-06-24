package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CalorieCalculatorService calorieCalculatorService;

    @Mock
    private DailySummaryService dailySummaryService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setNombre("Test User");
        testUser.setEmail("test@example.com");
        testUser.setContraseñaHash("password123");
        testUser.setEdad(25);
        testUser.setPesoKg(70.0);
        testUser.setAlturaCm(175.0);
        testUser.setGenero("hombre");
        testUser.setNivelActividad("media");
        testUser.setObjetivo("mantener");
    }

    @Test
    void testFindByEmail_UserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testFindByEmail_UserNotExists() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void testExistsByEmail_UserExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void testExistsByEmail_UserNotExists() {
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        boolean result = userService.existsByEmail("nonexistent@example.com");

        assertFalse(result);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }
} 