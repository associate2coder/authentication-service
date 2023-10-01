package ua.com.associate2coder.authenticationservice.users.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.associate2coder.authenticationservice.common.exceptions.ElementNotFoundException;
import ua.com.associate2coder.authenticationservice.entities.Role;
import ua.com.associate2coder.authenticationservice.users.repositories.RoleRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    @DisplayName("#1 Searching for correct role should pass")
    public void testGetRole1() {
        Mockito.when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(new Role("ADMIN")));
        Role role = roleService.getRole("ADMIN");
        Assertions.assertEquals("ADMIN", role.getName());
    }

    @Test
    @DisplayName("#2 Searching for incorrect role should throw exception")
    public void testGetRole2() {
        Mockito.when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        String incorrectRole = "SUPER_ADMIN";
        Assertions.assertThrows(ElementNotFoundException.class, () -> roleService.getRole("incorrectRole"), "Role not found with name: " + incorrectRole );
    }
}
