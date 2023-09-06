package ua.com.associate2coder.authenticationservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VerificationToken {

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Temporal(TemporalType.DATE)
    private Date created;
    @Temporal(TemporalType.DATE)
    private Date expired;

    public boolean isExpired() {
        return new Date(System.currentTimeMillis()).after(expired);
    }
}
