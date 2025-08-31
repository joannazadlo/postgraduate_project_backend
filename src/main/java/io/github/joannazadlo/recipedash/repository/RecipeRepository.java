package io.github.joannazadlo.recipedash.repository;

import io.github.joannazadlo.recipedash.repository.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByUserUid(String uid);

    @Query("SELECT recipe FROM Recipe recipe JOIN FETCH recipe.user")
    List<Recipe> findAllWithUser();
}
