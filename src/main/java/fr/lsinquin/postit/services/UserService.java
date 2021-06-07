package fr.lsinquin.postit.services;

import fr.lsinquin.postit.domain.exceptions.ExistingUserException;
import fr.lsinquin.postit.repositories.UserRepository;
import fr.lsinquin.postit.domain.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    /**
     * Creates and persists a new user
     * @param mail mail address
     * @param password raw password
     * @return {@link fr.lsinquin.postit.domain.entities.User created user}
     * @throws ExistingUserException raised when a user with the same mail address already exists
     */
    @Transactional(dontRollbackOn = { ExistingUserException.class })
    public User createUser(String mail, String password) throws ExistingUserException {
        log.info("Creating new user {}", mail);

        if(userRepository.existsByMail(mail)) {
            log.debug("A user of mail {} already exists", mail);

            throw new ExistingUserException(mail);
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setMail(mail);
        user.setPassword(hashedPassword);

        return userRepository.saveAndFlush(user);
    }
}
