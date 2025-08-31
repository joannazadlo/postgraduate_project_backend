package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.user.StatusDto;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.model.user.UserSignUpDto;
import io.github.joannazadlo.recipedash.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "API related to user account management and profile")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserSignUpDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{uid}")
    public ResponseEntity<UserDto> getUserByUid(@PathVariable @NotBlank String uid) {
        return ResponseEntity.ok(userService.getUserByUid(uid));
    }

    @PatchMapping("/{uid}/status")
    public ResponseEntity<UserDto> updateUserStatus(
            @PathVariable @NotBlank String uid,
            @Valid @RequestBody StatusDto statusDto) {
        UserDto updatedUser = userService.updateUserStatus(uid, statusDto.getStatus());
        return ResponseEntity.ok(updatedUser);
    }
}
