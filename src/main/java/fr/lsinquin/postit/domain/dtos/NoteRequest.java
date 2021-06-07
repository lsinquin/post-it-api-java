package fr.lsinquin.postit.domain.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
public class NoteRequest {
    @NotNull(message = "Le champ title est obligatoire")
    private final String title;

    @NotNull(message = "Le champ content est obligatoire")
    private final String content;
}
