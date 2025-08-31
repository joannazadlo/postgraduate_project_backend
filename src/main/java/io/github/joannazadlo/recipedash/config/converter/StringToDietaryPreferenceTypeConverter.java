package io.github.joannazadlo.recipedash.config.converter;

import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class StringToDietaryPreferenceTypeConverter implements Converter<String, DietaryPreferenceType> {

    @Override
    public @NonNull DietaryPreferenceType convert(@NonNull String source) {
        return DietaryPreferenceType.fromDisplayName(source);
    }
}
