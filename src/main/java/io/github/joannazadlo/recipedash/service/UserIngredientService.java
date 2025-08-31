package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.exception.user.UserNotFoundException;
import io.github.joannazadlo.recipedash.exception.userIngredient.UserIngredientNotFoundException;
import io.github.joannazadlo.recipedash.mapper.UserIngredientMapper;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientDto;
import io.github.joannazadlo.recipedash.repository.UserIngredientRepository;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.User;
import io.github.joannazadlo.recipedash.repository.entity.UserIngredient;
import io.github.joannazadlo.recipedash.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserIngredientService {

    private final UserIngredientMapper userIngredientMapper;
    private final UserIngredientRepository userIngredientRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public UserIngredientDto createUserIngredient(UserIngredientCreateDto dto) {
        UserDto currentUser = SecurityUtils.getCurrentUser();

        UserIngredient userIngredientToSave = userIngredientMapper.toEntity(dto);

        User user = userRepository.findById(currentUser.getUid())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userIngredientToSave.setUser(user);

        UserIngredient saved = userIngredientRepository.save(userIngredientToSave);
        return userIngredientMapper.toDto(saved);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<UserIngredientDto> getUserIngredients() {
        UserDto currentUser = SecurityUtils.getCurrentUser();

        List<UserIngredient> ingredients = userIngredientRepository.findByUserUid(currentUser.getUid());

        return ingredients.stream()
                .map(userIngredientMapper::toDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void deleteUserIngredient(Long id) {
        UserIngredient ingredient = userIngredientRepository.findById(id)
                .orElseThrow(() -> new UserIngredientNotFoundException("Ingredient not found: " + id));

        String userId = SecurityUtils.getCurrentUser().getUid();
        if (!ingredient.getUser().getUid().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to delete this ingredient");
        }

        userIngredientRepository.deleteById(id);
    }
}
