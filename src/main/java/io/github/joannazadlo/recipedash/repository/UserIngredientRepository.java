package io.github.joannazadlo.recipedash.repository;

import io.github.joannazadlo.recipedash.repository.entity.UserIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserIngredientRepository extends JpaRepository<UserIngredient, Long> {
    List<UserIngredient> findByUserUid(String uid);
}
