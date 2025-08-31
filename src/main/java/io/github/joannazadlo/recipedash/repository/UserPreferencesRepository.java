package io.github.joannazadlo.recipedash.repository;

import io.github.joannazadlo.recipedash.repository.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, String> {
}
