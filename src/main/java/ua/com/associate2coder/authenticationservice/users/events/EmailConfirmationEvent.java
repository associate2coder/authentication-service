package ua.com.associate2coder.authenticationservice.users.events;

import org.springframework.context.ApplicationEvent;
import ua.com.associate2coder.authenticationservice.entities.User;

public class EmailConfirmationEvent extends ApplicationEvent {
    public EmailConfirmationEvent(User user) {
        super(user);
    }
}
