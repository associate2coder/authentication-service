package ua.com.associate2coder.authenticationservice.users.events;

import org.springframework.context.ApplicationEvent;
import ua.com.associate2coder.authenticationservice.entities.User;

public class PasswordResetEvent extends ApplicationEvent {
    public PasswordResetEvent(User user) {
        super(user);
    }
}
