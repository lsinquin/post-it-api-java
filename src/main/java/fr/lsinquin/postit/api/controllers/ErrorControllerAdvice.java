package fr.lsinquin.postit.api.controllers;

import fr.lsinquin.postit.domain.dtos.ErrorCode;
import fr.lsinquin.postit.domain.dtos.FieldErrorDetail;
import fr.lsinquin.postit.domain.exceptions.ExistingUserException;
import fr.lsinquin.postit.domain.exceptions.NoteNotFoundException;
import fr.lsinquin.postit.domain.dtos.ErrorResponse;
import fr.lsinquin.postit.domain.exceptions.AuthorizationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized Exceptions handler.
 * Besides the HTTP status, the API answers with a standardized error body {@link fr.lsinquin.postit.domain.dtos.ErrorResponse}
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ErrorControllerAdvice {

    /**
     * MethodArgumentNotValidExceptions handling method. This exception is usually raised when the body validation process failed.
     * @param exception instance of MethodArgumentNotValidException raised
     * @return a 400 HTTP response supported by an instance of ErrorResponse specifying the error code (ERR_INPUT_VALIDATION) and validation details
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException exception) {
        log.debug("A MethodArgumentNotValidException has been raised. Sending appropriate response");
        log.debug("Exception message : {}", exception.getMessage());

        List<FieldErrorDetail> details = exception.getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDetail(error.getDefaultMessage(), error.getField()))
                .collect(Collectors.toList());

        return new ErrorResponse(true, ErrorCode.ERR_INPUT_VALIDATION, details);
    }

    /**
     * NoteNotFoundException handling method. This exception is usually raised when a user tried to access a not existing note.
     * It returns a 404 HTTP response with no specific body
     * @param exception instance of NoteNotFoundException raised
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoteNotFoundException.class)
    public void handleNoteNotFoundException(NoteNotFoundException exception) {
        log.debug("A NoteNotFoundException has been raised. Sending appropriate response");
        log.debug("Exception message : {}", exception.getMessage());
    }

    /**
     * AuthorizationException handling method. This exception is usually raised when a user tried to access or modify a resource he has no access to.
     * The choice of not sending back a 403 HTTP response has been made to hide used id from users. It returns a 404 HTTP response with no specific body
     * @param exception instance of AuthorizationException raised
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AuthorizationException.class)
    public void handleAuthorizationException(AuthorizationException exception) {
        log.debug("A AuthorizationException has been raised. Sending appropriate response");
        log.debug("Exception message : {}", exception.getMessage());
    }

    /**
     * ExistingUserException handling method. This exception is usually raised when a user with a specific mail already exists during a user creation process.
     * @param exception instance of AuthorizationException raised
     * @return a 400 HTTP response supported by an instance of ErrorResponse specifying the error code (ERR_EXISTING_USER)
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExistingUserException.class)
    public ErrorResponse handleExistingUserException(ExistingUserException exception) {
        log.debug("A ExistingUserException has been raised. Sending appropriate response");
        log.debug("Exception message : {}", exception.getMessage());

        List<FieldErrorDetail> details = Collections.emptyList();

        return new ErrorResponse(true, ErrorCode.ERR_EXISTING_USER, details);
    }
}
