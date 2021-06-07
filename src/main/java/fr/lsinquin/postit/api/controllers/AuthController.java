package fr.lsinquin.postit.api.controllers;

import fr.lsinquin.postit.domain.dtos.UserRequest;
import fr.lsinquin.postit.api.security.CustomUserDetails;
import fr.lsinquin.postit.api.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;

    private final AuthenticationManager authenticationManager;

    /**
     * POST /login endpoint.
     * It logs in a user retrieving a JWT to use.
     * @param userDto {@link fr.lsinquin.postit.domain.dtos.UserRequest UserRequest} representing the parsed payload
     * @return String representing a JWT.
     */
    @PostMapping("login")
    public ResponseEntity<String> login(@Valid @RequestBody UserRequest userDto) {
        log.info("Handling login request");

        try {
            UserDetails userDetails = authenticateUser(userDto.getMail(), userDto.getPassword());

            String token = jwtTokenUtil.generateAccessToken(userDetails.getUsername());

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            token
                    )
                    .body(token);
        } catch (BadCredentialsException exception) {
            log.debug("Authentication failed with message : {}", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private UserDetails authenticateUser(String mail, String password) throws BadCredentialsException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(mail, password);

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        return (CustomUserDetails) authenticate.getPrincipal();
    }
}
