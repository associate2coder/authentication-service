package ua.com.associate2coder.authenticationservice.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "roles")
@Builder
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Role(String name) {
        this.name = name;
    }

    public String getAuthority() {
        return "ROLE_" + this.name;
    }
}
