package io.github.joannazadlo.recipedash.repository.entity;

import io.github.joannazadlo.recipedash.model.enums.UserOpinion;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "opinions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "recipe_id", "recipe_source"})
})
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "uid", nullable = false)
    private User user;

    @Column(name = "recipe_id", nullable = false)
    private String recipeId;

    @Column(name = "recipe_source", nullable = false)
    private String recipeSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_opinion")
    private UserOpinion userOpinion;
}
