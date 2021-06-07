package fr.lsinquin.postit.domain.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private final boolean error;
    private final ErrorCode errorCode;
    private final List<FieldErrorDetail> details;
}
