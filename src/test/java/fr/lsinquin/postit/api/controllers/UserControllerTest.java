package fr.lsinquin.postit.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.lsinquin.postit.domain.dtos.UserRequest;
import fr.lsinquin.postit.domain.exceptions.ExistingUserException;
import fr.lsinquin.postit.domain.entities.User;
import fr.lsinquin.postit.api.security.JwtTokenUtil;
import fr.lsinquin.postit.api.security.CustomUserDetailsService;
import fr.lsinquin.postit.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for endpoints defined in UserController.
 */
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtTokenUtil tokenUtil;

    private final String validMail = "test@test.com";
    private final String validPassword = "secret123";

    @Test
    @DisplayName("Test POST /users - Valid")
    public void testPostUserValid() throws Exception{
        var input = new UserRequest(validMail, validPassword);

        User savedUser = new User(51, validMail, validPassword, true);

        when(userService.createUser(validMail, validPassword)).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mail").value(validMail));

        verify(userService).createUser(validMail, validPassword);
    }

    @Test
    @DisplayName("Test POST /users - Validation error (no mail field)")
    public void testPostUserNullMail() throws Exception {
        UserRequest input = new UserRequest(null, validPassword);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details.length()").value(1))
                .andExpect(jsonPath("$.details[0].field").value("mail"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test POST /users - Validation error (blank mail)")
    public void testPostUserBlankMail() throws Exception {
        UserRequest input = new UserRequest("", validPassword);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details.length()").value(1))
                .andExpect(jsonPath("$.details[0].field").value("mail"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test POST /users - Validation error (invalid mail)")
    public void testPostUserInvalidMail() throws Exception {
        UserRequest input = new UserRequest("invalidmail", validPassword);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details.length()").value(1))
                .andExpect(jsonPath("$.details[0].field").value("mail"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test POST /users - Validation error (no password field)")
    public void testPostUserNullPassword() throws Exception {
        UserRequest input = new UserRequest(validMail, null);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details.length()").value(1))
                .andExpect(jsonPath("$.details[0].field").value("password"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test POST /users - Validation error (blank password)")
    public void testPostUserBlankPassword() throws Exception {
        UserRequest input = new UserRequest(validMail, "");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details.length()").value(1))
                .andExpect(jsonPath("$.details[0].field").value("password"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test POST /users - Validation error (invalid password)")
    public void testPostUserInvalidPassword() throws Exception {
        UserRequest input = new UserRequest(validMail, "short");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details.length()").value(1))
                .andExpect(jsonPath("$.details[0].field").value("password"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test POST /users - Multiple validation errors")
    public void testPostUserMultipleInvalidErrors() throws Exception {
        UserRequest input = new UserRequest("invalidmail", null);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details.length()").value(2));
    }

    @Test
    @DisplayName("Test POST /users - User already exists")
    public void testPostUserExistingUserError() throws Exception{
        var input = new UserRequest(validMail, validPassword);

        when(userService.createUser(validMail, validPassword)).thenThrow(ExistingUserException.class);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_EXISTING_USER"));

        verify(userService).createUser(validMail, validPassword);
    }
}
