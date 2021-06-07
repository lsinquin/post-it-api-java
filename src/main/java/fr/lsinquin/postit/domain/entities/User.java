package fr.lsinquin.postit.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "app_user")
@Getter @Setter @NoArgsConstructor
public class User {

    @Id
    @SequenceGenerator(
            name = "user_id_seq",
            sequenceName = "app_user_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    private Integer id;

    private String mail;

    private String password;

    @Column(name = "enabled")
    private Boolean isEnabled;

    public User(String mail, String password, Boolean isEnabled) {
        this.mail = mail;
        this.password = password;
        this.isEnabled = isEnabled;
    }

    public User(Integer id, String mail, String password, Boolean isEnabled) {
        this.id = id;
        this.mail = mail;
        this.password = password;
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(mail, user.mail) && Objects.equals(password, user.password) && Objects.equals(isEnabled, user.isEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mail, password, isEnabled);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", mail='" + mail + '\'' +
                '}';
    }
}
