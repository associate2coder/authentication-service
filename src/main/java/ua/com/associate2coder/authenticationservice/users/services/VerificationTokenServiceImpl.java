package ua.com.associate2coder.authenticationservice.users.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.associate2coder.authenticationservice.common.exceptions.VerificationException;
import ua.com.associate2coder.authenticationservice.users.repositories.VerificationTokenRepository;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService{

    private final VerificationTokenRepository tokenRepo;

    @Value(value = "${verification-token-validity-in-milliseconds}")
    private long VERIFICATION_TOKEN_VALIDITY;

    @Override
    public VerificationToken createVerificationToken(User user) {

        deleteExpiredTokensIfAny(user);
        VerificationToken token = buildToken(user, new Date(System.currentTimeMillis()));
        return tokenRepo.save(token);
    }

    private  VerificationToken buildToken(User user, Date date) {
        return VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .created(date)
                .expired(calculateExpiredDate(date))
                .build();
    }

    @Override
    public List<VerificationToken> getVerificationTokens(User user) {
        List<VerificationToken> existingTokens = tokenRepo.findAllByUserId(user.getId());
        if (existingTokens.isEmpty()) {
            throw new VerificationException("Verification tokes were not found");
        }
        return existingTokens;
    }

    @Transactional
    @Override
    public void deleteVerificationToken(User user, VerificationToken token) {
        if (tokenRepo.existsById(token.getId())) {
            tokenRepo.delete(token);
        }
    }

    @Transactional
    public void deleteExpiredTokensIfAny(User user) {
        List<VerificationToken> expiredTokens = tokenRepo.findAllByUserId(user.getId()).stream()
                .filter(VerificationToken::isExpired)
                .toList();

        if (!expiredTokens.isEmpty()) {
            expiredTokens.forEach(
                    usr -> tokenRepo.removeAllByUserId(usr.getId())
            );
        }
    }

    private Date calculateExpiredDate(Date creationDate) {
        return new Date(creationDate.getTime() + VERIFICATION_TOKEN_VALIDITY);
    }
}
