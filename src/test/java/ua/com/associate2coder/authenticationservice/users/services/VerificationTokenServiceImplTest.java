package ua.com.associate2coder.authenticationservice.users.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.associate2coder.authenticationservice.common.exceptions.ElementNotFoundException;
import ua.com.associate2coder.authenticationservice.entities.Role;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;
import ua.com.associate2coder.authenticationservice.users.repositories.VerificationTokenRepository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class VerificationTokenServiceImplTest {

    @Mock
    private VerificationTokenRepository tokenRepo;

    @InjectMocks
    private VerificationTokenServiceImpl tokenService;

    private static final long TOKEN_DURATION = 86400000;
    private static User testUser;
    private static Date testCreationDate;
    private static Date testExpiredDate;

    private static VerificationToken testToken;


    @BeforeAll
    public static void init() {

        long milliseconds = System.currentTimeMillis();
        testCreationDate = new Date(milliseconds);
        testExpiredDate = new Date(milliseconds + TOKEN_DURATION);

        testUser = User.builder()
                .email("test@gmail.com")
                .password("password")
                .firstName("First Name")
                .lastName("Last Name")
                .role(new Role("ADMIN"))
                .emailVerified(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .enabled(true)
                .build();

        testToken = VerificationToken.builder()
                .token("dab10563-ef3b-4cd8-9089-013ad8669c7d") //just a random UUID
                .user(testUser)
                .created(testCreationDate)
                .expired(testExpiredDate)
                .build();
    }


    @Test
    @DisplayName("#1 Verification token should have parseable UUID")
    public void testBuildToken1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = tokenService.getClass().getDeclaredMethod("buildToken", User.class, Date.class);
        method.setAccessible(true);
        VerificationToken token = (VerificationToken) method.invoke(tokenService, testUser, testCreationDate);
        Assertions.assertDoesNotThrow(() -> UUID.fromString(token.getToken()));
    }

    @Test
    @DisplayName("#2 Existing VerificationToken should be returned")
    public void testGetVerificationToken1() {
        when(tokenRepo.findAllByUserId(anyLong())).thenReturn(List.of(testToken));
        Assertions.assertDoesNotThrow(() -> tokenService.createVerificationToken(testUser));
    }

    @Test
    @DisplayName("#3 If VerificationToken is absent, ElementNotFoundException should be thrown")
    public void testGetVerificationToken2() {
        when(tokenRepo.findAllByUserId(testUser.getId())).thenReturn(List.of());
        Assertions.assertThrows(ElementNotFoundException.class, () -> tokenService.getVerificationTokens(testUser), "No verification tokens exist for the user");
    }

    @Test
    @DisplayName("#4 Method should return correct expiry date (issue date + duration)")
    public void testCalculateExpiredDate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {

        Field field = tokenService.getClass().getDeclaredField("VERIFICATION_TOKEN_VALIDITY");
        field.setAccessible(true);
        field.set(tokenService, TOKEN_DURATION);

        Method method = tokenService.getClass().getDeclaredMethod("calculateExpiredDate", Date.class);
        method.setAccessible(true);
        Date expiredDate = (Date) method.invoke(tokenService, testCreationDate);
        Assertions.assertEquals(testExpiredDate, expiredDate);
    }
}