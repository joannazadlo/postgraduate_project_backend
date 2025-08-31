package io.github.joannazadlo.recipedash.model.user;

import io.github.joannazadlo.recipedash.model.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusDto {

    @NotNull(message = "Status must not be null")
    private Status status;
}
