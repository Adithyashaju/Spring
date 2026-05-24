package com.auth.authservice.repository;

import com.auth.authservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("john_doe", "john@example.com", "encoded", "USER", true);
    }

    @Test
    void save_PersistsUserSuccessfully() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User saved = userRepository.save(user);

        assertNotNull(saved);
        assertEquals("john_doe", saved.getUsername());
        assertEquals("john@example.com", saved.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findByUsername_ReturnsUser() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        Optional<User> found = userRepository.findByUsername("john_doe");

        assertTrue(found.isPresent());
        assertEquals("john_doe", found.get().getUsername());
        verify(userRepository, times(1)).findByUsername("john_doe");
    }

    @Test
    void findByUsername_ReturnsEmpty() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> found = userRepository.findByUsername("unknown");

        assertFalse(found.isPresent());
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void existsByUsername_ReturnsTrue() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertTrue(userRepository.existsByUsername("john_doe"));
        verify(userRepository, times(1)).existsByUsername("john_doe");
    }

    @Test
    void existsByUsername_ReturnsFalse() {
        when(userRepository.existsByUsername("unknown")).thenReturn(false);

        assertFalse(userRepository.existsByUsername("unknown"));
        verify(userRepository, times(1)).existsByUsername("unknown");
    }

    @Test
    void existsByEmail_ReturnsTrue() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertTrue(userRepository.existsByEmail("john@example.com"));
        verify(userRepository, times(1)).existsByEmail("john@example.com");
    }

    @Test
    void existsByEmail_ReturnsFalse() {
        when(userRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        assertFalse(userRepository.existsByEmail("unknown@example.com"));
        verify(userRepository, times(1)).existsByEmail("unknown@example.com");
    }

    @Test
    void findAll_ReturnsAllUsers() {
        User user2 = new User("jane_doe", "jane@example.com", "encoded2", "USER", true);
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<User> result = userRepository.findAll();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteById_DeletesSuccessfully() {
        doNothing().when(userRepository).deleteById(1L);

        userRepository.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
