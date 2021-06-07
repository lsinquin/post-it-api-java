package fr.lsinquin.postit.services;

import fr.lsinquin.postit.domain.entities.User;
import fr.lsinquin.postit.repositories.UserRepository;
import fr.lsinquin.postit.api.security.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CustomUserDetailsService
 */
@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    private final String mail = "test@test.com";

    @Test
    @DisplayName("Test loadUserByUsername() - Valid")
    public void testLoadUserByUsername() {
        when(userRepository.findByMail(mail)).thenReturn(Optional.of(generateUser()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(mail);

        assertEquals(userDetails.getUsername(), mail);
        assertEquals(userDetails.getAuthorities().size(), 1);

        verify(userRepository).findByMail(mail);
   }

    @Test
    @DisplayName("Test loadUserByUsername() - No user")
    public void testLoadUserByUsernameNoUser() {
        when(userRepository.findByMail(mail)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(mail));

        verify(userRepository).findByMail(mail);
    }

    private User generateUser() {
        return new User(51, mail, "secret12345", true);
    }
}
