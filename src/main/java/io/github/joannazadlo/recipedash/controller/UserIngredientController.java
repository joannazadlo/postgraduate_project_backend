package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientDto;
import io.github.joannazadlo.recipedash.service.UserIngredientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Ingredients", description = "API related to managing user-specific ingredients")
@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class UserIngredientController {

    private final UserIngredientService userIngredientService;

    @PostMapping
    public ResponseEntity<UserIngredientDto> createUserIngredient(@Valid @RequestBody UserIngredientCreateDto dto) {
        UserIngredientDto createdIngredient = userIngredientService.createUserIngredient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIngredient);
    }

    @GetMapping
    public ResponseEntity<List<UserIngredientDto>> getUserIngredients() {
        List<UserIngredientDto> ingredients = userIngredientService.getUserIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserIngredient(@PathVariable Long id) {
        userIngredientService.deleteUserIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
