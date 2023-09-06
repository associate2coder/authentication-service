package ua.com.associate2coder.authenticationservice.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserDeleteResponse {

    private Long id;
    private String email;
    private String message;
}
