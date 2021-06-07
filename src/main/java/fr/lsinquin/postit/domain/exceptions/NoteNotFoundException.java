package fr.lsinquin.postit.domain.exceptions;

import fr.lsinquin.postit.domain.entities.Note;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

/**
 * Exception to be raised when no note is found for a specific id.
 */
@Getter
public class NoteNotFoundException extends RuntimeException {

    /**
     * id which has no note associated with
     */
    private final Integer noteId;

    public NoteNotFoundException(Integer noteId) {
        super(format("Couldn't find note of id %d", noteId));
        this.noteId = noteId;
    }

    public NoteNotFoundException(String message, Throwable cause, Integer noteId) {
        super(message, cause);
        this.noteId = noteId;
    }

    public NoteNotFoundException(String message, Integer noteId) {
        super(message);
        this.noteId = noteId;
    }

    public NoteNotFoundException(Throwable cause, Integer noteId) {
        super(format("Couldn't find note of id %d", noteId), cause);
        this.noteId = noteId;
    }
}
