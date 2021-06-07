package fr.lsinquin.postit.repositories;

import fr.lsinquin.postit.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT count(user) > 0 from User user WHERE user.mail = :mail")
    public boolean existsByMail(String mail);

    @Query("SELECT user from User user WHERE user.mail = :mail")
    public Optional<User> findByMail(String mail);
}
