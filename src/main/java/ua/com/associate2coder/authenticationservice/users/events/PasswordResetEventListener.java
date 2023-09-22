package ua.com.associate2coder.authenticationservice.users.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ua.com.associate2coder.authenticationservice.email.EmailService;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;
import ua.com.associate2coder.authenticationservice.users.services.VerificationTokenService;

@Component
@RequiredArgsConstructor
public class PasswordResetEventListener {

    private final EmailService emailService;
    private final VerificationTokenService tokenService;

    @EventListener
    public void handlePasswordResetEvent(PasswordResetEvent event) {
        User user = (User) event.getSource();
        VerificationToken token = tokenService.createVerificationToken(user);
        emailService.sendPasswordResetEmail(user, user.getEmail(), token.getToken());
    }

}
