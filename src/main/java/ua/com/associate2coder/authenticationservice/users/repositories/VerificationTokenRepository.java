package ua.com.associate2coder.authenticationservice.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findAllByUserId(Long userId);

    boolean existsByUserId(Long userId);

    void removeAllByUserId(Long userId);



}
