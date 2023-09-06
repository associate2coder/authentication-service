package ua.com.associate2coder.authenticationservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany
    private List<User> users;

    public Role(String name) {
        this.name = name;
        this.users = new ArrayList<>();
    }

    public String getAuthority() {
        return "ROLE_" + this.name;
    }
}
