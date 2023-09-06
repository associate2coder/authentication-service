package ua.com.associate2coder.authenticationservice.authentication;

public interface AuthenticationService {
    AuthenticationResponse register(UserRegistrationRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

}
