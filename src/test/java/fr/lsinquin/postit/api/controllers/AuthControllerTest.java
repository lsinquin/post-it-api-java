package fr.lsinquin.postit.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.lsinquin.postit.domain.dtos.UserRequest;
import fr.lsinquin.postit.domain.entities.User;
import fr.lsinquin.postit.api.security.CustomUserDetails;
import fr.lsinquin.postit.api.security.JwtTokenUtil;
import fr.lsinquin.postit.api.security.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for endpoints defined in AuthController.
 */
@WebMvcTest(controllers = AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private final String validMail = "test@mail.com";
    private final String validPassword = "secret12345";

    @Test
    @DisplayName("Test POST /login - Valid")
    public void testLoginValid() throws Exception{
        var input = new UserRequest(validMail, validPassword);

        when(jwtTokenUtil.generateAccessToken(validMail)).thenReturn("ojojazeoajeozaejao.ajozjeoazjeozeajeoa8542.jdjdpzedjpaojpa2542");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(new UsernamePasswordAuthenticationToken(generateUserDetails(), null));

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(jsonPath("$").isString());

        verify(jwtTokenUtil).generateAccessToken(validMail);
        verify(authenticationManager).authenticate(any(Authentication.class));
    }

    @Test
    @DisplayName("Test POST /login - Validation error (blank mail)")
    public void testLoginBlankMail() throws Exception {
        UserRequest input = new UserRequest("", validPassword);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details[0].field").value("mail"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test POST /login - Validation error (invalid mail)")
    public void testLoginInvalidMail() throws Exception {
        UserRequest input = new UserRequest("invalidmail", validPassword);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details[0].field").value("mail"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test POST /login - Validation error (blank password)")
    public void testLoginBlankPassword() throws Exception {
        UserRequest input = new UserRequest(validMail, "");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details[0].field").value("password"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test POST /login - Validation error (invalid password)")
    public void testLoginInvalidPassword() throws Exception {
        UserRequest input = new UserRequest(validMail, "short");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details[0].field").value("password"))
                .andExpect(jsonPath("$.details[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("Test POST /login - Multiple validation errors")
    public void testLoginMultipleInvalidErrors() throws Exception {
        UserRequest input = new UserRequest("", "");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_INPUT_VALIDATION"))
                .andExpect(jsonPath("$.details.length()").value(2));
    }

    private UserDetails generateUserDetails() {
        User user = new User(25, validMail, "jadjpazjdpzap", true);

        return new CustomUserDetails(user);
    }
}
