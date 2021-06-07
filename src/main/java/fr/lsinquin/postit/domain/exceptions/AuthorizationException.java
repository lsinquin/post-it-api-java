package fr.lsinquin.postit.domain.exceptions;

import lombok.Getter;

import static java.lang.String.format;

/**
 * Exception to be raised when a user is not authorized to access a specific note
 */
@Getter
public class AuthorizationException extends RuntimeException {

    /**
     * mail address of the not authorized user.
     */
    private final String notAuthorizedUser;

    public AuthorizationException(String notAuthorizedUser) {
        super(format("The user of mail %s is not authorized", notAuthorizedUser));
        this.notAuthorizedUser = notAuthorizedUser;
    }

    public AuthorizationException(String message, Throwable cause, String notAuthorizedUser) {
        super(message, cause);
        this.notAuthorizedUser = notAuthorizedUser;
    }

    public AuthorizationException(String message, String notAuthorizedUser) {
        super(message);
        this.notAuthorizedUser = notAuthorizedUser;
    }

    public AuthorizationException(Throwable cause, String notAuthorizedUser) {
        super(format("The user of mail %s is not authorized", notAuthorizedUser), cause);
        this.notAuthorizedUser = notAuthorizedUser;
    }
}
