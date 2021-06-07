package fr.lsinquin.postit.services;

import fr.lsinquin.postit.domain.exceptions.NoteNotFoundException;
import fr.lsinquin.postit.domain.exceptions.AuthorizationException;
import fr.lsinquin.postit.domain.entities.Note;
import fr.lsinquin.postit.domain.entities.User;
import fr.lsinquin.postit.repositories.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for NoteService
 */
@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @InjectMocks
    private NoteService noteService;

    @Mock
    private NoteRepository noteRepository;

    private final String title = "testing title";
    private final String content = "testing content";

    @Test
    @DisplayName("Test createNote() - Valid")
    public void testCreateNote() {
        when(noteRepository.saveAndFlush(Mockito.any(Note.class))).thenReturn(generateNote());

        Note savedNote = noteService.createNote(generateUser(), title, content);

        assertNotNull(savedNote);
        assertEquals(content, savedNote.getContent());

        verify(noteRepository).saveAndFlush(Mockito.any(Note.class));
    }

    @Test
    @DisplayName("Test findNote() - Valid")
    public void testFindNote() {
        when(noteRepository.findById(51)).thenReturn(Optional.of(generateNote()));

        Note foundNote = noteService.findNote(generateUser(), 51);

        assertNotNull(foundNote);
        verify(noteRepository).findById(51);


    }

    @Test
    @DisplayName("Test findNote() - Not authorized")
    public void testFindNoteAuthorizationException() {
        User notAuthorizedUser = generateUser(15);
        when(noteRepository.findById(51)).thenReturn(Optional.of(generateNote()));

        assertThrows(AuthorizationException.class, () -> noteService.findNote(notAuthorizedUser, 51));
        verify(noteRepository).findById(51);
    }

    @Test
    @DisplayName("Test findNote() - No note")
    public void testFindNoteNotFound() {
        when(noteRepository.findById(51)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, () -> noteService.findNote(generateUser(), 51));
        verify(noteRepository).findById(51);
    }

    @Test
    @DisplayName("Test findUserNotes() - Valid")
    public void testFindUserNotes() {
        when(noteRepository.findNotesByUser(25)).thenReturn(generateNotes());

        List<Note> notes = noteService.findUserNotes(generateUser());

        assertEquals(5, notes.size());
        verify(noteRepository).findNotesByUser(25);
    }

    @Test
    @DisplayName("Test findUserNotes() - Empty result")
    public void testFindUserNotesNoNote() {
        when(noteRepository.findNotesByUser(25)).thenReturn(Collections.emptyList());

        List<Note> notes = noteService.findUserNotes(generateUser());

        assertEquals(0, notes.size());
        verify(noteRepository).findNotesByUser(25);
    }

    @Test
    @DisplayName("Test modifyNote() - Valid")
    public void testModifyNote() {
        var newTitle = "modified title";
        var newContent = "modified content";
        var resultNote = new Note(51, newTitle, newContent, generateUser());

        when(noteRepository.findById(51)).thenReturn(Optional.of(generateNote()));
        when(noteRepository.saveAndFlush(Mockito.any(Note.class))).thenReturn(resultNote);

        Note modifiedNote = noteService.modifyNote(generateUser(), 51, newTitle, newContent);

        assertEquals(newTitle, modifiedNote.getTitle());
        assertEquals(newContent, modifiedNote.getContent());

        verify(noteRepository).findById(51);
        verify(noteRepository).saveAndFlush(Mockito.any(Note.class));
    }

    @Test
    @DisplayName("Test modifyNote() - Not authorized")
    public void testModifyNoteAuthorizationException() {
        User notAuthorizedUser = generateUser(15);
        when(noteRepository.findById(51)).thenReturn(Optional.of(generateNote()));

        assertThrows(AuthorizationException.class, () -> noteService.modifyNote(notAuthorizedUser, 51, "title", "content"));
        verify(noteRepository).findById(51);
    }

    @Test
    @DisplayName("Test modifyNote() - No note")
    public void testModifyNoteNotFound() {
        when(noteRepository.findById(51)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, () -> noteService.modifyNote(generateUser(), 51, "title", "content"));
        verify(noteRepository).findById(51);
    }

    @Test
    @DisplayName("Test deleteNote() - Valid")
    public void testDeleteNote() {
        when(noteRepository.findById(51)).thenReturn(Optional.of(generateNote()));

        noteService.deleteNote(generateUser(), 51);

        verify(noteRepository).findById(51);
        verify(noteRepository).deleteById(51);
    }

    @Test
    @DisplayName("Test deleteNote() - Not authorized")
    public void testDeleteNoteAuthorizationException() {
        User notAuthorizedUser = generateUser(16);
        when(noteRepository.findById(51)).thenReturn(Optional.of(generateNote()));

        assertThrows(AuthorizationException.class, () -> noteService.deleteNote(notAuthorizedUser, 51));
        verify(noteRepository).findById(51);
    }

    @Test
    @DisplayName("Test deleteNote() - No note")
    public void testDeleteNoteNotFound() {
        when(noteRepository.findById(51)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, () -> noteService.deleteNote(generateUser(), 51));
        verify(noteRepository).findById(51);
    }

    private User generateUser(Integer id) {
        return new User(id, "valid@mail.com", "secret123", true);
    }

    private User generateUser() {
        return new User(25, "valid@mail.com", "secret123", true);
    }

    private Note generateNote() {
        return new Note(51, title, content, generateUser());
    }

    private List<Note> generateNotes() {
        return List.of(1, 2, 3, 4, 5)
                .stream()
                .map(n -> new Note(n, format("note %s", n), format("content %s", n), generateUser()))
                .collect(Collectors.toList());
    }
}
