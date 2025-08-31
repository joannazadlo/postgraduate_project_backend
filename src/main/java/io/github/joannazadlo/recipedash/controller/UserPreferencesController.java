package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.userPreferences.UserPreferencesDto;
import io.github.joannazadlo.recipedash.service.UserPreferencesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Preferences", description = "API related to managing user dietary preferences and settings")
@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;

    @PutMapping
    public ResponseEntity<UserPreferencesDto> saveUserPreferences(@RequestBody UserPreferencesDto userPreferences) {
        return ResponseEntity.ok(userPreferencesService.saveUserPreferences(userPreferences));
    }

    @GetMapping
    public ResponseEntity<UserPreferencesDto> getUserPreferences() {
        return ResponseEntity.ok(userPreferencesService.getUserPreferences());
    }
}
