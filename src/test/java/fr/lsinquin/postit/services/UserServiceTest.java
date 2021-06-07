package fr.lsinquin.postit.services;

import fr.lsinquin.postit.domain.exceptions.ExistingUserException;
import fr.lsinquin.postit.domain.entities.User;
import fr.lsinquin.postit.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    private final String mail = "test@test.com";
    private final String password = "secret12345";
    private final String hashedPassword = "zejfzpjfpzefjoepzjfozp";

    @Test
    @DisplayName("Test createUser() - Valid")
    public void testCreateUser() {
        when(userRepository.existsByMail(mail)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);
        when(userRepository.saveAndFlush(Mockito.any(User.class))).thenReturn(generateUser());

        User savedUser = userService.createUser(mail, password);

        assertNotNull(savedUser.getId());
        assertEquals(mail, savedUser.getMail());
        assertEquals(hashedPassword, savedUser.getPassword());

        verify(userRepository).existsByMail(mail);
        verify(passwordEncoder).encode(password);
        verify(userRepository).saveAndFlush(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Test createUser() - User already exists")
    public void testCreateExistingUser() {
        when(userRepository.existsByMail(mail)).thenReturn(true);

        assertThrows(ExistingUserException.class, () -> userService.createUser(mail, password));

        verify(userRepository).existsByMail(mail);
    }

    private User generateUser() {
        return new User(51, mail, hashedPassword, true);
    }
}
