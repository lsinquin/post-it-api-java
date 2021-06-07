package fr.lsinquin.postit.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.lsinquin.postit.domain.dtos.NoteRequest;
import fr.lsinquin.postit.domain.exceptions.AuthorizationException;
import fr.lsinquin.postit.domain.exceptions.NoteNotFoundException;
import fr.lsinquin.postit.domain.entities.Note;
import fr.lsinquin.postit.domain.entities.User;
import fr.lsinquin.postit.api.security.CustomUserDetails;
import fr.lsinquin.postit.api.security.JwtTokenUtil;
import fr.lsinquin.postit.services.NoteService;
import fr.lsinquin.postit.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for endpoints defined in NoteController.
 */
@WebMvcTest(controllers = NoteController.class)
@WithUserDetails(value = "valid@mail.com")
public class NoteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoteService noteService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtTokenUtil tokenUtil;

    private final String title = "Un titre de test";
    private final String content = "Contenu super intéressant qui peut être plus ou moins long";

    @PostConstruct
    void setUp() {
        when(userDetailsService.loadUserByUsername("valid@mail.com")).thenReturn(new CustomUserDetails(generateUser()));
    }

    @Test
    @DisplayName("Test GET /notes - Valid")
    public void testGetUserNotes() throws Exception {
        when(noteService.findUserNotes(generateUser())).thenReturn(generateNotes());

        mockMvc.perform(get("/notes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5));

        verify(noteService).findUserNotes(generateUser());
    }

    @Test
    @DisplayName("Test GET /notes - Empty result")
    public void testGetUserNotesEmptyResult() throws Exception {
        when(noteService.findUserNotes(generateUser())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/notes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(noteService).findUserNotes(generateUser());
    }

    @Test
    @DisplayName("Test POST /notes - Valid")
    public void testPostNoteValid() throws Exception{
        var input = new NoteRequest(title, content);

        when(noteService.createNote(generateUser(), title, content)).thenReturn(generateNote(51));

        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content));

        verify(noteService).createNote(generateUser(), title, content);
    }

    @Test
    @DisplayName("Test POST /notes - Allow blank values")
    public void testPostNoteAllowBlankValues() throws Exception {
        var input = new NoteRequest("", "");
        Note createdNote = new Note(51, "", "", generateUser());

        when(noteService.createNote(generateUser(), "", "")).thenReturn(createdNote);

        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(""));

        verify(noteService).createNote(generateUser(), "", "");
    }

    @Test
    @DisplayName("Test POST /notes - Validation error (no content field)")
    public void testPostNoteNullValidation() throws Exception {
        var input = new NoteRequest(title, null);

        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details[0].field").value("content"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test PUT /notes/:id - Valid")
    public void testPutNoteValid() throws Exception{
        var input = new NoteRequest(title, content);

        Note modifiedNote = generateNote(51);

        when(noteService.modifyNote(generateUser(), 51, title, content)).thenReturn(modifiedNote);

        mockMvc.perform(put("/notes/{id}", 51)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(51))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content));

        verify(noteService).modifyNote(generateUser(), 51, title, content);
    }

    @Test
    @DisplayName("Test PUT /notes/:id - Not authorized")
    public void testPutNoteNotAuthorized() throws Exception {
        var input = new NoteRequest(title, content);

        when(noteService.modifyNote(generateUser(), 51, title, content)).thenThrow(AuthorizationException.class);

        mockMvc.perform(put("/notes/{id}", 51)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test PUT /notes/:id - Allow blank values")
    public void testPutNoteAllowBlankValues() throws Exception {
        var input = new NoteRequest("", "");

        Note modifiedNote = new Note(51, "", "", generateUser());

        when(noteService.modifyNote(generateUser(),51,"","")).thenReturn(modifiedNote);

        mockMvc.perform(put("/notes/{id}", 51)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(51))
                .andExpect(jsonPath("$.title").value(""))
                .andExpect(jsonPath("$.content").value(""));

        verify(noteService).modifyNote(generateUser(),51,"","");
    }

    @Test
    @DisplayName("Test PUT /notes/:id - Validation error (no title field)")
    public void testPutNoteNullValidation() throws Exception {
        var input = new NoteRequest(null, content);

        mockMvc.perform(put("/notes/{id}", 51)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details[0].field").value("title"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test GET /notes/:id - Valid")
    public void testGetNoteById() throws Exception {
        when(noteService.findNote(generateUser(), 51)).thenReturn(generateNote(51));

        mockMvc.perform(get("/notes/{id}", 51)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(51))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content));

        verify(noteService).findNote(generateUser(), 51);
    }

    @Test
    @DisplayName("Test GET /notes/:id - No note")
    public void testGetNotFoundNoteById() throws Exception {
        when(noteService.findNote(generateUser(), 51)).thenThrow(NoteNotFoundException.class);

        mockMvc.perform(get("/notes/{id}", 51)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(noteService).findNote(generateUser(), 51);
    }

    @Test
    @DisplayName("Test GET /notes/:id - Not authorized")
    public void testGetNoteNotAuthorized() throws Exception {
        when(noteService.findNote(generateUser(), 51)).thenThrow(AuthorizationException.class);

        mockMvc.perform(get("/notes/{id}", 51)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(noteService).findNote(generateUser(), 51);
    }

    @Test
    @DisplayName("Test DELETE /notes/:id - Valid")
    public void testDeleteNote() throws Exception {
        mockMvc.perform(delete("/notes/{id}", 51)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(noteService).deleteNote(generateUser(), 51);
    }

    @Test
    @DisplayName("Test DELETE /notes/:id - No note")
    public void testDeleteNotFoundNote() throws Exception {
        doThrow(NoteNotFoundException.class).when(noteService).deleteNote(generateUser(), 51);

        mockMvc.perform(delete("/notes/{id}", 51)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(noteService).deleteNote(generateUser(), 51);
    }

    @Test
    @DisplayName("Test DELETE /notes/:id - Not authorized")
    public void testDeleteNoteNotAuthorized() throws Exception {
        doThrow(AuthorizationException.class).when(noteService).deleteNote(generateUser(), 51);

        mockMvc.perform(delete("/notes/{id}", 51)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(noteService).deleteNote(generateUser(), 51);
    }

    private User generateUser() {
        return new User(25, "valid@mail.com", "secret123", true);
    }

    private Note generateNote(Integer id) {
        return new Note(id, title, content, generateUser());
    }

    private List<Note> generateNotes() {
        return List.of(1, 2, 3, 4, 5)
                .stream()
                .map(n -> new Note(n, format("note %s", n), format("content %s", n), generateUser()))
                .collect(Collectors.toList());
    }
}
