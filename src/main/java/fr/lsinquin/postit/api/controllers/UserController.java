package fr.lsinquin.postit.api.controllers;

import fr.lsinquin.postit.domain.dtos.UserRequest;
import fr.lsinquin.postit.domain.dtos.UserResponse;
import fr.lsinquin.postit.domain.entities.User;
import fr.lsinquin.postit.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * POST /users endpoint.
     * It creates a new user
     * @param userDto {@link fr.lsinquin.postit.domain.dtos.UserRequest UserRequest} representing the parsed payload
     * @return {@link fr.lsinquin.postit.domain.dtos.UserResponse UserResponse} representing the created user
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse postUser(@Valid @RequestBody UserRequest userDto) {
        log.info("Handling posting new user");

        User savedUser = userService.createUser(userDto.getMail(), userDto.getPassword());

        return new UserResponse(savedUser.getMail());
    }
}
