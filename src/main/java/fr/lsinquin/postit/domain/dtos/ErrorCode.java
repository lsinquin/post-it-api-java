package fr.lsinquin.postit.domain.dtos;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ERR_EXISTING_USER, ERR_INPUT_VALIDATION;
}
