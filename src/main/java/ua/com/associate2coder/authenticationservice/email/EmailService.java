package ua.com.associate2coder.authenticationservice.email;

import ua.com.associate2coder.authenticationservice.entities.User;

import java.util.List;

public interface EmailService {

    void sendPlainTextEmail(String from, String to, String subject, List<String> messages, boolean debug);

    void sendEmailConfirmationEmail(User user, String userEmail, String token);
}
