package ua.com.associate2coder.authenticationservice.email;

import jakarta.annotation.PostConstruct;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.com.associate2coder.authenticationservice.entities.User;

import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final Properties props = new Properties();

    private final String HOST = "https://localhost:8443";
    private final String smtpUsername = System.getenv().get("SMTP_USER");   // NEED ENV VARIABLE
    private final String smtpPassword = System.getenv().get("SMTP_PASSWORD");   // NEED ENV VARIABLE
    private final String smtpHost = System.getenv().get("SMTP_HOST"); // NEED ENV VARIABLE


    @PostConstruct
    public void init() {
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.ssl.trust", smtpHost);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    }

    @Override
    public void sendPlainTextEmail(String from, String to, String subject, List<String> messages, boolean debug) {

        Authenticator authenticator = getAuthenticator();

        Session session = Session.getInstance(props, authenticator);
        session.setDebug(debug);

        try {

            // create a message with headers
            MimeMessage msg = prepareMessage(session, from, to, subject);
            // create message body
            setMessageContent(msg, messages);
            // send the message
            Transport.send(msg);

        } catch (MessagingException mex) {
            mex.printStackTrace();
            Exception ex = null;
            if ((ex = mex.getNextException()) != null) {
                ex.printStackTrace();
            }
        }
    }

    private Authenticator getAuthenticator() {
        return new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        };
    }

    private MimeMessage prepareMessage(Session session, String from, String to, String subject) throws MessagingException {
        // create a message with headers
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        InternetAddress[] address = {new InternetAddress(to)};
        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        return msg;
    }

    private void setMessageContent(MimeMessage msg, List<String> messages) throws MessagingException {
        // create message body
        Multipart mp = new MimeMultipart();
        for (String message : messages) {
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText(message, "us-ascii", "html");
            mp.addBodyPart(mbp);
        }
        msg.setContent(mp);
    }


    @Override
    public void sendEmailConfirmationEmail(User user, String userEmail, String token) {
        final String from = "no-reply@domain.com";
        final String subject = "E-mail confirmation";
        final String url = String.format(HOST + "/api/v1/email/confirmation/%s/%s", user.getId(), token);
        String link = String.format("<a href=\"%s\">%s</a>", url, url);
        final String text = String.format("<p>Dear %s!</p>" +
                "<p>Please confirm your e-mail address by following the link below:</p>" +
                        "<p>%s</p>" +
                        "<p>Thank you!<p>",
                user.getFirstName(),
                link);

        sendPlainTextEmail
                (
                        from,
                        userEmail,
                        subject,
                        List.of(text),
                        true
                );
    }
}