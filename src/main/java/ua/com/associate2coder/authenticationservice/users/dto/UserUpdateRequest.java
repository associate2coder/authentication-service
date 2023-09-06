package ua.com.associate2coder.authenticationservice.users.dto;

import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
        @Email(message = "Username (e-mail) should be a valid e-mail address")
        String email,
        String updateEmail,
        String updateFirstName,
        String updateLastName
) {


}
