package ua.com.associate2coder.authenticationservice.users.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.com.associate2coder.authenticationservice.authentication.UserRegistrationRequest;
import ua.com.associate2coder.authenticationservice.common.exceptions.BadRequestException;
import ua.com.associate2coder.authenticationservice.common.exceptions.ElementNotFoundException;
import ua.com.associate2coder.authenticationservice.entities.Role;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;
import ua.com.associate2coder.authenticationservice.users.dto.CustomMessageResponse;
import ua.com.associate2coder.authenticationservice.users.dto.SetNewPasswordRequest;
import ua.com.associate2coder.authenticationservice.users.repositories.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private VerificationTokenService tokenService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    private static User correctUser;
    private static Role userRole;
    private static VerificationToken correctToken;
    private static VerificationToken expiredToken;
    private static final long DAY_IN_MILLISECONDS = 24*60*60*1000;
    private static final long TOKEN_DURATION = 86400000;

    private static final String NON_EXISTING_TOKEN = "1ed06130-a668-4174-8af3-8ab6612e7a23";




    @BeforeAll
    public static void init() {

        correctUser = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("password")
                .firstName("First Name")
                .lastName("Last Name")
                .role(new Role("USER"))
                .emailVerified(false)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .enabled(true)
                .build();


        userRole = Role.builder()
                .id(1L)
                .name("USER")
                .build();

        correctToken = VerificationToken.builder()
                .token("dab10563-ef3b-4cd8-9089-013ad8669c7d") //just a random UUID
                .user(correctUser)
                .created(new Date(System.currentTimeMillis()))
                .expired(new Date(System.currentTimeMillis() + TOKEN_DURATION))
                .build();

        expiredToken = VerificationToken.builder()
                .token("dab11111-ef1b-1cd1-1111-111ad1111c1d") //just a random UUID
                .user(correctUser)
                .created(new Date(System.currentTimeMillis() - DAY_IN_MILLISECONDS))
                .expired(new Date(System.currentTimeMillis() - DAY_IN_MILLISECONDS + TOKEN_DURATION))
                .build();
    }


    // CREATE USER TESTS
    @Test
    @DisplayName("user is being created when provided with a correct request")
    public void createUserTest() {
        UserRegistrationRequest request = new UserRegistrationRequest(correctUser.getEmail(), correctUser.getPassword(), correctUser.getFirstName(), correctUser.getLastName());
        Mockito.when(roleService.getRole("USER")).thenReturn(userRole);
        User anyUser = Mockito.any(User.class);
        Mockito.when(userRepository.save(anyUser)).thenReturn(anyUser);
        User user = userService.createUser(request);

        Assertions.assertEquals(correctUser.getEmail(), user.getEmail());
        Assertions.assertEquals(correctUser.getUsername(), user.getUsername());
        Assertions.assertEquals(correctUser.getFirstName(), user.getFirstName());
        Assertions.assertEquals(correctUser.getLastName(), user.getLastName());
        Assertions.assertEquals(correctUser.getRole().getAuthority(), user.getRole().getAuthority());
    }

    @Test
    @DisplayName("Existing user (searched by email) should NOT throw a 404 exception")
    public void getUserByEmailTest1() {
        String testEmail = "test@email.com";
        Mockito.when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        Assertions.assertThrows(ElementNotFoundException.class, () -> userService.getUserByEmail(testEmail));
    }


    @Test
    @DisplayName("non-existing user should throw 404 exception")
    public void getUserByEmailTest2() {
        String testEmail = "test@email.com";
        Mockito.when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(correctUser));
        Assertions.assertDoesNotThrow(() -> userService.getUserByEmail(testEmail));
    }

    // GET USER TESTS

    @Test
    @DisplayName("Existing user (search by ID of long type) should NOT throw a 404 exception")
    public void getUserByIdTest1() {
        long testId = 1L;
        Mockito.when(userRepository.findById(testId)).thenReturn(Optional.of(correctUser));
        Assertions.assertDoesNotThrow(() -> userService.getUserById(testId));
    }

    @Test
    @DisplayName("Non-existing user (search by ID of long type) should throw a 404 exception")
    public void getUserByIdTest2() {
        long testId = 1L;
        Mockito.when(userRepository.findById(testId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ElementNotFoundException.class, () -> userService.getUserById(testId));
    }

    @Test
    @DisplayName("Existing user (search by ID of String type) should NOT throw a 404 exception")
    public void getUserByIdTest3() {
        String testId = "1";
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(correctUser));
        Assertions.assertDoesNotThrow(() -> userService.getUserById(testId));
    }

    @Test
    @DisplayName("Non-existing user (search by ID of String type) should throw a 404 exception")
    public void getUserByIdTest4() {
        String testId = "1";
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ElementNotFoundException.class, () -> userService.getUserById(testId));
    }

    // DELETE USER TESTS
    // will be conducted via integration tests

    // USER UPDATE TESTS
    // will be conducted via integration tests

    // CONFIRM EMAIL TESTS
    @Test
    @DisplayName("Correct token should pass")
    public void confirmEmailTest1() {
        String testId = "1";

        Mockito.when(tokenService.getVerificationTokens(correctUser)).thenReturn(List.of(correctToken, expiredToken));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(correctUser));

        CustomMessageResponse message = userService.confirmEmail(testId, correctToken.getToken());
        Assertions.assertEquals("Email has been successfully confirmed!", message.getMessage());
        Assertions.assertDoesNotThrow(() -> userService.confirmEmail(testId, correctToken.getToken()));

    }

    @Test
    @DisplayName("Expired token should fail")
    public void confirmEmailTest2() {
        String testId = "1";

        Mockito.when(tokenService.getVerificationTokens(correctUser)).thenReturn(List.of(correctToken, expiredToken));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(correctUser));

        Assertions.assertThrows(BadRequestException.class, () -> userService.confirmEmail(testId, expiredToken.getToken()));
    }

    @Test
    @DisplayName("Non-existing token should fail")
    public void confirmEmailTest3() {
        String testId = "1";

        Mockito.when(tokenService.getVerificationTokens(correctUser)).thenReturn(List.of(correctToken, expiredToken));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(correctUser));

        Assertions.assertThrows(BadRequestException.class, () -> userService.confirmEmail(testId, NON_EXISTING_TOKEN));
    }


    // SET NEW PASSWORD TESTS

    @Test
    @DisplayName("Correct token should pass")
    public void setNewPasswordTest1() {
        String newPassword = "12345678";

        Mockito.when(tokenService.getVerificationTokens(correctUser)).thenReturn(List.of(correctToken, expiredToken));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(correctUser));

        SetNewPasswordRequest request = new SetNewPasswordRequest(1L, newPassword, expiredToken.getToken());
        Assertions.assertThrows(BadRequestException.class, () -> userService.setNewPassword(request));

    }

    @Test
    @DisplayName("Expired token should fail")
    public void setNewPasswordTest2() {
        String newPassword = "12345678";

        Mockito.when(tokenService.getVerificationTokens(correctUser)).thenReturn(List.of(correctToken, expiredToken));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(correctUser));

        SetNewPasswordRequest request = new SetNewPasswordRequest(1L, newPassword, expiredToken.getToken());

        Assertions.assertThrows(BadRequestException.class, () -> userService.setNewPassword(request));
    }

    @Test
    @DisplayName("Non-existing token should fail")
    public void setNewPasswordTest3() {
        String newPassword = "12345678";

        Mockito.when(tokenService.getVerificationTokens(correctUser)).thenReturn(List.of(correctToken, expiredToken));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(correctUser));

        SetNewPasswordRequest request = new SetNewPasswordRequest(1L, newPassword, NON_EXISTING_TOKEN);

        Assertions.assertThrows(BadRequestException.class, () -> userService.setNewPassword(request));
    }
}

