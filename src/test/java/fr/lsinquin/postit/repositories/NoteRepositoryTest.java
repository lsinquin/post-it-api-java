package fr.lsinquin.postit.repositories;

import fr.lsinquin.postit.domain.entities.Note;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NoteRepository. Testing only query methods.
 * The tests are run on a H2 in memory database which is initialized by the data.sql file.
 */
@DataJpaTest
public class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Test
    @DisplayName("Test findNotesByUser() - Valid")
    public void testFindNotesByUser() throws Exception {
        List<Note> notes = noteRepository.findNotesByUser(1);

        assertEquals(5, notes.size());
    }

    @Test
    @DisplayName("Test findNotesByUser() - Empty result")
    public void testFindNotesByUserNoResult() throws Exception {
        List<Note> notes = noteRepository.findNotesByUser(3);

        assertNotNull(notes);
        assertEquals(0, notes.size());
    }
}