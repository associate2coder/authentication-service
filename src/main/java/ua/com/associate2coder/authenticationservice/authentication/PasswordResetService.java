package ua.com.associate2coder.authenticationservice.authentication;

import ua.com.associate2coder.authenticationservice.users.dto.CustomMessageResponse;
import ua.com.associate2coder.authenticationservice.users.dto.PasswordResetRequest;
import ua.com.associate2coder.authenticationservice.users.dto.PasswordResetResponse;

public interface PasswordResetService {

    CustomMessageResponse requestPasswordReset(PasswordResetRequest request);

    PasswordResetResponse resetPassword(String id, String token);
}
