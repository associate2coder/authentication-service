package ua.com.associate2coder.authenticationservice.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ua.com.associate2coder.authenticationservice.common.exceptions.BadRequestException;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;
import ua.com.associate2coder.authenticationservice.users.dto.CustomMessageResponse;
import ua.com.associate2coder.authenticationservice.users.dto.PasswordResetRequest;
import ua.com.associate2coder.authenticationservice.users.dto.PasswordResetResponse;
import ua.com.associate2coder.authenticationservice.users.events.PasswordResetEvent;
import ua.com.associate2coder.authenticationservice.users.services.UserService;
import ua.com.associate2coder.authenticationservice.users.services.VerificationTokenService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserService userService;
    private final VerificationTokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public CustomMessageResponse requestPasswordReset(PasswordResetRequest request) {
        User user = userService.getUserByEmail(request.email());
        eventPublisher.publishEvent(new PasswordResetEvent(user));
        return CustomMessageResponse.builder()
                .message("Confirmation request has been sent to " + user.getEmail())
                .build();
    }

    // Once this resetPassword worked, User should provide his/her new password (via frontend)

    @Override
    public PasswordResetResponse resetPassword(String id, String tokenString) {

        User user = userService.getUserById(id);
        List<VerificationToken> tokens = tokenService.getVerificationTokens(user);

        for (VerificationToken token: tokens) {
            if (tokenString.equals(token.getToken()) && !token.isExpired()) {
                return PasswordResetResponse.builder()
                        .message("Please enter your new password!") // frontend should provide a form allowing user to submit a new password
                        .userId(user.getId())
                        .token(tokenString)
                        .build();
            }
        }
        log.info("Attempt to reset password failed");
        throw new BadRequestException("Password is not reset!");
    }
}
