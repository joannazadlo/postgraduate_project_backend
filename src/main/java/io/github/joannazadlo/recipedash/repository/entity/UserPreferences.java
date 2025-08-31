package io.github.joannazadlo.recipedash.repository.entity;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    @Column(name = "user_id")
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "uid", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(name = "user_preferred_ingredients", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "preferred_ingredients")
    private List<String> preferredIngredients;

    @Enumerated(EnumType.STRING)
    private CuisineType cuisine;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_dietary_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "dietary_preferences")
    private List<DietaryPreferenceType> dietaryPreferences;

    @Column(name = "exclude_disliked")
    private Boolean excludeDisliked;
}
