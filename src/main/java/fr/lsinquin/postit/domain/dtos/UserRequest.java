package fr.lsinquin.postit.domain.dtos;

import lombok.Data;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;

@Data
public class UserRequest {

    @NotBlank(message = "Le champ mail est obligatoire")
    @Email(message = "Le mail doit être valide")
    private final String mail;

    @NotNull(message = "Le champ password est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit comporter au minimum 8 caractères")
    private final String password;
}
