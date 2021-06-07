package fr.lsinquin.postit.services;

import fr.lsinquin.postit.domain.exceptions.AuthorizationException;
import fr.lsinquin.postit.domain.exceptions.NoteNotFoundException;
import fr.lsinquin.postit.repositories.NoteRepository;
import fr.lsinquin.postit.domain.entities.Note;
import fr.lsinquin.postit.domain.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Note service
 * All the methods of this class need a {@link fr.lsinquin.postit.domain.entities.User asking user} as a parameter to be able to decide if the user is authorized or not.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;

    /**
     * Creates and persists a new note
     * @param user {@link fr.lsinquin.postit.domain.entities.User asking user}
     * @param title title. It can be blank
     * @param content content. It can be blank
     * @return {@link fr.lsinquin.postit.domain.entities.Note created note}
     */
    public Note createNote(User user, String title, String content) {
        log.info("Creating new note for user {}", user.getMail());

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setUser(user);

        return noteRepository.saveAndFlush(note);
    }

    /**
     * Looks for for a specific note
     * @param user {@link fr.lsinquin.postit.domain.entities.User asking user}
     * @param id id of the wanted note
     * @return {@link fr.lsinquin.postit.domain.entities.Note wanted note}
     * @throws NoteNotFoundException raised if no note was found
     * @throws AuthorizationException raised if the asking user can't access this note : if it is not one of his notes
     */
    public Note findNote(User user, Integer id) throws NoteNotFoundException, AuthorizationException {
        log.info("Finding note of id {} for user {}", id, user.getMail());

        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));

        if(isNotAuthorized(user, note)) {
            log.debug("User {} not authorized", user.getMail());

            throw new AuthorizationException(user.getMail());
        }

        return note;
    }

    /**
     * Looks for all notes of a specific user
     * @param user {@link fr.lsinquin.postit.domain.entities.User asking user}
     * @return List of {@link fr.lsinquin.postit.domain.entities.Note Note}
     */
    public List<Note> findUserNotes(User user) {
        log.info("Finding notes for user {}", user.getMail());

        return noteRepository.findNotesByUser(user.getId());
    }

    /**
     * Modifies a specific note.
     * @param user {@link fr.lsinquin.postit.domain.entities.User asking user}
     * @param id id of the to be modified note
     * @param title title. It can be blank
     * @param content content. It can be blank
     * @return {@link fr.lsinquin.postit.domain.entities.Note modified note}
     * @throws NoteNotFoundException raised if no note exists for this id
     * @throws AuthorizationException raised if the asking user can't access this note : if it is not one of his notes
     */
    @Transactional(dontRollbackOn = { NoteNotFoundException.class })
    public Note modifyNote(User user, Integer id, String title, String content) throws NoteNotFoundException, AuthorizationException {
        log.info("Modifying note of id {} for user {}", id, user.getMail());

        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));

        if(isNotAuthorized(user, note)) {
            log.debug("User {} not authorized", user.getMail());

            throw new AuthorizationException(user.getMail());
        }

        note.setTitle(title);
        note.setContent(content);

        return noteRepository.saveAndFlush(note);
    }

    /**
     * Deleted a specific note
     * @param user {@link fr.lsinquin.postit.domain.entities.User asking user}
     * @param id id of the to be deleted note
     * @throws NoteNotFoundException raised if no note exists for this id
     * @throws AuthorizationException raised if the asking user can't access this note : if it is not one of his notes
     */
    @Transactional(dontRollbackOn = { NoteNotFoundException.class })
    public void deleteNote(User user, Integer id) throws NoteNotFoundException, AuthorizationException {
        log.info("Deleting note of id {} for user {}", id, user.getMail());

        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));

        if(isNotAuthorized(user, note)) {
            log.debug("User {} not authorized", user.getMail());

            throw new AuthorizationException(user.getMail());
        }

        noteRepository.deleteById(id);

    }

    /**
     * Decides if a user is authorized to access a note.
     * A user is authorized to access a note only if it is one of his notes
     * @param user {@link fr.lsinquin.postit.domain.entities.User asking user}
     * @param user {@link fr.lsinquin.postit.domain.entities.User to be accessed note}
     * @return True if the user authorized to access the note. False otherwise
     */
    private boolean isNotAuthorized(User user, Note note) {
        return !(note.getUser().getId().equals(user.getId()));
    }
}
