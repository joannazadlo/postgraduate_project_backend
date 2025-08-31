package io.github.joannazadlo.recipedash.model.rating;

import io.github.joannazadlo.recipedash.model.enums.UserOpinion;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveOpinionDto {

    @NotNull(message = "User opinion is required")
    private UserOpinion userOpinion;
}
