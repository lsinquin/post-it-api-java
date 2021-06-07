package fr.lsinquin.postit.api.security;

import fr.lsinquin.postit.domain.entities.User;
import fr.lsinquin.postit.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of the {@link org.springframework.security.core.userdetails.UserDetailsService UserDetailsService interface}.
 * It's responsible of loading a user from a username.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user using {@link #userRepository}.
     * @param s Username of the user. The username is his mail address.
     * @return {@inheritDoc}
     * @throws UsernameNotFoundException {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByMail(s).orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur pour le mail " + s));

        return new CustomUserDetails(user);
    }
}
