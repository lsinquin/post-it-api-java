package fr.lsinquin.postit.domain.dtos;

import lombok.Data;

@Data
public class FieldErrorDetail {
    private final String message;
    private final String field;
}
