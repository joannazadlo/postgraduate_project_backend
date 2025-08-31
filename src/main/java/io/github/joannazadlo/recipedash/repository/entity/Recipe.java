package io.github.joannazadlo.recipedash.repository.entity;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "image_source")
    private String imageSource;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<RecipeIngredient> ingredients;

    @ElementCollection
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "steps", length = 5000)
    private List<String> steps;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "uid", nullable = false)
    private User user;

    @Column(name = "is_public", nullable = false)
    private boolean publicRecipe;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "recipe_dietary_preferences", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "dietary_preference")
    private List<DietaryPreferenceType> dietaryPreferences;

    private String cookingTime;

    @Enumerated(EnumType.STRING)
    private CuisineType cuisine;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
