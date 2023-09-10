package ua.com.associate2coder.authenticationservice.users.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public record UserDeleteRequest
        (
                @Email(message = "Username (e-mail) should be a valid e-mail address")
                String email
        ) {


}
