package ua.com.associate2coder.authenticationservice.users.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.associate2coder.authenticationservice.users.repositories.VerificationTokenRepository;
import ua.com.associate2coder.authenticationservice.common.exceptions.ElementNotFoundException;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceimpl implements VerificationTokenService{

    private final VerificationTokenRepository tokenRepo;
    @Value(value = "${verification-token-validity-in-milliseconds}")
    private long VERIFICATION_TOKEN_VALIDITY;
    @Override
    public VerificationToken createVerificationToken(User user) {

        deleteExistingTokensIfAny(user);

        final Date date = new Date(System.currentTimeMillis());

        VerificationToken token = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .created(date)
                .expired(calculateExpiredDate(date))
                .build();
        return tokenRepo.save(token);
    }

    @Override
    public VerificationToken getVerificationToken(User user) {
        return tokenRepo.findAllByUserId(user.getId())
                .orElseThrow(() -> new ElementNotFoundException("No verification tokens exist for the user"));
    }

    @Transactional
    @Override
    public void deleteVerificationTokens(User user) {
        final long userId = user.getId();
        if (tokenRepo.existsByUserId(userId)) {
            tokenRepo.removeAllByUserId(userId);
        }
    }

    @Transactional
    public void deleteExistingTokensIfAny(User user) {
        tokenRepo.findAllByUserId(user.getId()).ifPresent(usr -> tokenRepo.removeAllByUserId(usr.getId()));
    }

    private Date calculateExpiredDate(Date creationDate) {
        return new Date(creationDate.getTime() + VERIFICATION_TOKEN_VALIDITY);
    }
}
