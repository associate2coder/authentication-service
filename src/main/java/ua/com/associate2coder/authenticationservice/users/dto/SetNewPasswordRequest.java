package ua.com.associate2coder.authenticationservice.users.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SetNewPasswordRequest(

        Long id,

        @Size(min = 8, message = "Password should be at least 8 characters long")
        String password,

        @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", message = "Token string does not comply with token parameters")
        String token

        ) {


}
