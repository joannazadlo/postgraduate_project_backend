package io.github.joannazadlo.recipedash.model.tasty;

import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalIngredientDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TastyRecipeRaw {
    private String canonical_id;
    private String name;
    private String thumbnail_url;
    private List<Instruction> instructions;
    private List<Section> sections;
    private List<TastyTag> tags;

    public List<String> extractInstructions() {
        if (instructions == null) return List.of();
        return instructions.stream()
                .map(Instruction::getDisplay_text)
                .toList();
    }

    public List<ExternalIngredientDto> extractIngredients() {
        if (sections == null) return List.of();

        return sections.stream()
                .flatMap(section -> section.getComponents().stream())
                .map(component -> {
                    ExternalIngredientDto ingredientDto = new ExternalIngredientDto();
                    if (component.getIngredient() != null) {
                        ingredientDto.setName(component.getIngredient().getName());
                    } else {
                        ingredientDto.setName("");
                    }

                    if (component.getMeasurements() != null && !component.getMeasurements().isEmpty()) {
                        var measurement = component.getMeasurements().get(0);
                        String quantity = measurement.getQuantity() != null ? measurement.getQuantity() : "";
                        String unit = measurement.getUnit() != null ? measurement.getUnit().getName() : "";
                        ingredientDto.setQuantity((quantity + " " + unit).trim());
                    } else {
                        ingredientDto.setQuantity("");
                    }

                    return ingredientDto;
                })
                .toList();
    }
}
