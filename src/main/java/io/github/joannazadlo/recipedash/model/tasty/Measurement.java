package io.github.joannazadlo.recipedash.model.tasty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Measurement {
    private String quantity;
    private Unit unit;
}
