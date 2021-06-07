package fr.lsinquin.postit.repositories;

import fr.lsinquin.postit.domain.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Integer> {

    @Query("SELECT note FROM Note note, User user WHERE note.user = user AND user.id = :id ")
    public List<Note> findNotesByUser(Integer id);
}
