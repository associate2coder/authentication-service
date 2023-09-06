package ua.com.associate2coder.authenticationservice.authentication;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest (
        @Email(message = "Username (e-mail) should be a valid e-mail address")
        String email,

        @Size(min = 8, message = "Password should be at least 8 characters long")
        String password,

        @NotBlank(message = "First name cannot be empty")
        String firstName,

        @NotBlank(message = "Last name cannot be empty")
        String lastName
) {

}
