package io.github.joannazadlo.recipedash.model.tasty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Component {
    private Ingredient ingredient;
    private List<Measurement> measurements;
}
