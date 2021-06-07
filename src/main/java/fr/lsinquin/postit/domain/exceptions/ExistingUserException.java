package fr.lsinquin.postit.domain.exceptions;

import lombok.Getter;

import static java.lang.String.format;

/**
 * Exception to be raised during the user creation process when a user already exists with the same mail.
 */
@Getter
public class ExistingUserException extends RuntimeException {

    /**
     * Mail address of the existing user
     */
    private final String existingUserMail;

    public ExistingUserException(String existingUserMail) {
        super(format("A user of mail %s already exists", existingUserMail));
        this.existingUserMail = existingUserMail;
    }

    public ExistingUserException(String message, Throwable cause, String existingUserMail) {
        super(message, cause);
        this.existingUserMail = existingUserMail;
    }

    public ExistingUserException(String message, String existingUserMail) {
        super(message);
        this.existingUserMail = existingUserMail;
    }

    public ExistingUserException(Throwable cause, String existingUserMail) {
        super(format("A user of mail %s already exists", existingUserMail), cause);
        this.existingUserMail = existingUserMail;
    }
}
