package ua.com.associate2coder.authenticationservice.users.services;

import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;

import java.util.List;

public interface VerificationTokenService {

    VerificationToken createVerificationToken(User user);

    List<VerificationToken> getVerificationTokens(User user);

    void deleteVerificationToken(User user, VerificationToken token);
}
