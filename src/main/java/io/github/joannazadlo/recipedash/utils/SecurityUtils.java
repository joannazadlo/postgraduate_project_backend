package io.github.joannazadlo.recipedash.utils;

import io.github.joannazadlo.recipedash.model.user.UserDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {}

    public static UserDto getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDto user)) {
            throw new AccessDeniedException("Unauthorized access");
        }

        return user;
    }
}
