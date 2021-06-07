package fr.lsinquin.postit.domain.dtos;

import lombok.*;

@Data
public class NoteResponse {
    private final Integer id;
    private final String title;
    private final String content;
}
