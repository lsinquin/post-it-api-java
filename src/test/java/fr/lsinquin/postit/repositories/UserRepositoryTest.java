package fr.lsinquin.postit.repositories;

import fr.lsinquin.postit.domain.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserRepository. Testing only query methods.
 * The tests are run on a H2 in memory database which is initialized by the data.sql file..
 */
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final String mail = "test@mail.com";
    private final String notFoundMail = "wrong@mail.com";

    @Test
    @DisplayName("Test findByMail() - Valid")
    public void testFindByMail() throws Exception {
        Optional<User> opt = userRepository.findByMail(mail);

        assertTrue(opt.isPresent());
        assertEquals(mail, opt.get().getMail());
    }

    @Test
    @DisplayName("Test findByMail() - Empty result")
    public void testFindByMailNoResult() throws Exception {
        Optional<User> opt = userRepository.findByMail(notFoundMail);

        assertTrue(opt.isEmpty());
    }

    @Test
    @DisplayName("Test existsByMail() - True")
    public void testExistsByMail() throws Exception {
        assertTrue(userRepository.existsByMail(mail));
    }

    @Test
    @DisplayName("Test existsByMail() - False")
    public void testExistsByMailNotExist() throws Exception {
        assertFalse(userRepository.existsByMail(notFoundMail));
    }
}
