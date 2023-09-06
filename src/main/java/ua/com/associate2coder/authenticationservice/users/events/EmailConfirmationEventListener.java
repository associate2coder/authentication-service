package ua.com.associate2coder.authenticationservice.users.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ua.com.associate2coder.authenticationservice.users.services.VerificationTokenService;
import ua.com.associate2coder.authenticationservice.email.EmailService;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;

@Component
@RequiredArgsConstructor
public class EmailConfirmationEventListener {

    private final EmailService emailService;
    private final VerificationTokenService tokenService;

    @EventListener
    public void handleEmailChangeEvent(EmailConfirmationEvent event) {
        User user = (User) event.getSource();
        VerificationToken token = tokenService.createVerificationToken(user);
        emailService.sendEmailConfirmationEmail(user, user.getEmail(), token.getToken());
    }

}
