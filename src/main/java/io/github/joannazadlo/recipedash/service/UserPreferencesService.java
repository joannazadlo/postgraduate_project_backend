package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.exception.user.UserNotFoundException;
import io.github.joannazadlo.recipedash.mapper.UserPreferencesMapper;
import io.github.joannazadlo.recipedash.model.userPreferences.UserPreferencesDto;
import io.github.joannazadlo.recipedash.repository.UserPreferencesRepository;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.UserPreferences;
import io.github.joannazadlo.recipedash.repository.entity.User;
import io.github.joannazadlo.recipedash.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPreferencesService {

    private final UserPreferencesMapper mapper;
    private final UserPreferencesRepository repository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Transactional
    public UserPreferencesDto saveUserPreferences(UserPreferencesDto userPreferencesDto) {
        String uid = SecurityUtils.getCurrentUser().getUid();

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserPreferences userPreferencesToSave = repository.findById(uid)
                .orElseGet(() -> UserPreferences.builder()
                        .user(user)
                        .build());

        userPreferencesToSave.setPreferredIngredients(
                userPreferencesDto.getPreferredIngredients() != null
                        ? userPreferencesDto.getPreferredIngredients()
                        : List.of()
        );

        userPreferencesToSave.setCuisine(userPreferencesDto.getCuisine());

        userPreferencesToSave.setDietaryPreferences(
                userPreferencesDto.getDietaryPreferences() != null
                        ? userPreferencesDto.getDietaryPreferences()
                        : List.of()
        );

        userPreferencesToSave.setExcludeDisliked(userPreferencesDto.getExcludeDisliked());

        UserPreferences saved = repository.save(userPreferencesToSave);

        return mapper.toUserPreferencesDto(saved);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public UserPreferencesDto getUserPreferences() {
        String userId = SecurityUtils.getCurrentUser().getUid();
        return repository.findById(userId)
                .map(mapper::toUserPreferencesDto)
                .orElse(UserPreferencesDto.empty());
    }
}
