package io.github.joannazadlo.recipedash.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpDto {

    @NotBlank(message = "UID must not be blank")
    private String uid;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;

}
