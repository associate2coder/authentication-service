package ua.com.associate2coder.authenticationservice.users.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.com.associate2coder.authenticationservice.common.exceptions.ElementNotFoundException;
import ua.com.associate2coder.authenticationservice.entities.Role;
import ua.com.associate2coder.authenticationservice.users.repositories.RoleRepository;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @PostConstruct
    private void init() {
        Set<String> roles = Set.of
                (
                        "ADMINISTRATOR",
                        "SUPPORT",
                        "USER"
                );
        for (String roleString: roles) {
            Optional<Role> roleSearch = roleRepository.findByName(roleString);
            if (roleSearch.isEmpty()) {
                roleRepository.save(new Role(roleString));
            }
        }
    }

    @Override
    public Role getRole(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ElementNotFoundException("Role not found with name: " + name));
    }
}
