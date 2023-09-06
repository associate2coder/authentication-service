package ua.com.associate2coder.authenticationservice.authentication;

import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthenticationResponse {

    private String token;

}
