package ua.com.associate2coder.authenticationservice.users.services;

import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;

public interface VerificationTokenService {

    VerificationToken createVerificationToken(User user);

    VerificationToken getVerificationToken(User user);

    void deleteVerificationTokens(User user);


}
