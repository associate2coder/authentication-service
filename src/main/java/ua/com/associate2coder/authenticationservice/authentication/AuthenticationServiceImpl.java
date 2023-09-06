package ua.com.associate2coder.authenticationservice.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ua.com.associate2coder.authenticationservice.users.services.UserService;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(UserRegistrationRequest request) {
        return prepareAuthenticationResponse(userService.createUser(request));

    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        return prepareAuthenticationResponse(userService.getUser(request.email()));
    }

    private AuthenticationResponse prepareAuthenticationResponse(User user) {
        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

}
