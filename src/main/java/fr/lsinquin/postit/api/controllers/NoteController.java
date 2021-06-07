package fr.lsinquin.postit.api.controllers;

import fr.lsinquin.postit.domain.dtos.NoteRequest;
import fr.lsinquin.postit.domain.dtos.NoteResponse;
import fr.lsinquin.postit.domain.entities.Note;
import fr.lsinquin.postit.api.security.CustomUserDetails;
import fr.lsinquin.postit.services.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    private final NoteService noteService;

    /**
     * Secured GET /notes endpoint.
     * It retrieves authenticated user's notes
     * @param userDetails {@link fr.lsinquin.postit.api.security.CustomUserDetails Authenticated user}
     * @return List of {@link fr.lsinquin.postit.domain.dtos.NoteResponse NoteResponse}
     */
    @GetMapping()
    public List<NoteResponse> getNotesByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Handling getting user's notes");

        List<Note> notes = noteService.findUserNotes(userDetails.getUser());

        return notes.stream()
                .map(this::convertNoteToNoteResponse)
                .collect(toList());
    }

    /**
     * Secured POST /notes endpoint.
     * It created a new note for the authenticated user
     * @param userDetails {@link fr.lsinquin.postit.api.security.CustomUserDetails Authenticated user}
     * @param noteDto {@link fr.lsinquin.postit.domain.dtos.NoteRequest NoteRequest} representing the parsed payload
     * @return {@link fr.lsinquin.postit.domain.dtos.NoteResponse NoteResponse} representing the created note
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse postNote(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody NoteRequest noteDto) {
        log.info("Handling posting new note");

        Note savedNote = noteService.createNote(userDetails.getUser(), noteDto.getTitle(), noteDto.getContent());

        return convertNoteToNoteResponse(savedNote);
    }

    /**
     * Secured GET /notes/:id endpoint.
     * It retrieves a specific note
     * @param userDetails {@link fr.lsinquin.postit.api.security.CustomUserDetails Authenticated user}
     * @param id id of the wanted note
     * @return {@link fr.lsinquin.postit.domain.dtos.NoteResponse NoteResponse} representing wanted note
     */
    @GetMapping("/{id}")
    public NoteResponse getNoteById(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Integer id) {
        log.info("Handling getting a specific note");

        Note note = noteService.findNote(userDetails.getUser(), id);

        return convertNoteToNoteResponse(note);
    }

    /**
     * Secured PUT /notes/:id endpoint.
     * It modifies a specific note
     * @param userDetails {@link fr.lsinquin.postit.api.security.CustomUserDetails Authenticated user}
     * @param id id of the to be modifid note
     * @param noteDto {@link fr.lsinquin.postit.domain.dtos.NoteRequest NoteRequest} representing the parsed payload
     * @return {@link fr.lsinquin.postit.domain.dtos.NoteResponse NoteResponse} representing the modified note
     */
    @PutMapping("/{id}")
    public NoteResponse putNoteById(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Integer id, @Valid @RequestBody NoteRequest noteDto) {
        log.info("Handling putting a specific note");

        Note savedNote = noteService.modifyNote(userDetails.getUser(), id, noteDto.getTitle(), noteDto.getContent());

        return convertNoteToNoteResponse(savedNote);
    }

    /**
     * Secured DELETE /notes/:id endpoint.
     * It deletes a specific note
     * @param userDetails {@link fr.lsinquin.postit.api.security.CustomUserDetails Authenticated user}
     * @param id id of the to be deleted note
     */
    @DeleteMapping("/{id}")
    public void deleteNoteById(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Integer id) {
        log.info("Handling deleting a specific note");

        noteService.deleteNote(userDetails.getUser(), id);
    }

    /**
     * Mapper between a {@link fr.lsinquin.postit.domain.entities.Note Note entity} and a {@link fr.lsinquin.postit.domain.dtos.NoteResponse NoteResponse}
     * @param note {@link fr.lsinquin.postit.domain.entities.Note Note entity}
     * @return mapped {@link fr.lsinquin.postit.domain.dtos.NoteResponse NoteResponse}
     */
    private NoteResponse convertNoteToNoteResponse(Note note) {
        return new NoteResponse(note.getId(), note.getTitle(), note.getContent());
    }
}
