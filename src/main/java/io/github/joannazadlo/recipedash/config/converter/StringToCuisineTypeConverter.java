package io.github.joannazadlo.recipedash.config.converter;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import org.springframework.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCuisineTypeConverter implements Converter<String, CuisineType> {

    @Override
    public @NonNull CuisineType convert(@NonNull String source) {
        return CuisineType.fromDisplayName(source);
    }
}
