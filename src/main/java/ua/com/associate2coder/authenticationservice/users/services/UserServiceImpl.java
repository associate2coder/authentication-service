package ua.com.associate2coder.authenticationservice.users.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.com.associate2coder.authenticationservice.authentication.UserRegistrationRequest;
import ua.com.associate2coder.authenticationservice.common.exceptions.BadRequestException;
import ua.com.associate2coder.authenticationservice.entities.Role;
import ua.com.associate2coder.authenticationservice.common.exceptions.ElementNotFoundException;
import ua.com.associate2coder.authenticationservice.common.exceptions.StatusAlreadyAppliesException;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.entities.VerificationToken;
import ua.com.associate2coder.authenticationservice.users.dto.*;
import ua.com.associate2coder.authenticationservice.users.events.EmailConfirmationEvent;
import ua.com.associate2coder.authenticationservice.users.events.PasswordResetEvent;
import ua.com.associate2coder.authenticationservice.users.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + email));
    }

    @Override
    public User createUser(UserRegistrationRequest request) {
        Role role = roleService.getRole("USER");
        User newUser = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(role)
                .emailVerified(false)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .enabled(true)
                .build();
        userRepository.save(newUser);
        eventPublisher.publishEvent(new EmailConfirmationEvent(newUser));
        return newUser;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ElementNotFoundException("User not found with email: " + email));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("User not found with id #: " + id));
    }

    private User getUserById(String id) {
        long userId;
        try {
            userId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid id number");
        }
        return getUserById(userId);
    }


    @Override
    public UserDeleteResponse deleteUser(UserDeleteRequest request) {
        String email = request.email();
        User user = getUserByEmail(email);
        userRepository.delete(user);
        return UserDeleteResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .message("User has been deleted.")
                .build();
    }

    @Override
    public UserUpdateResponse updateUser(UserUpdateRequest request) {
        User user = getUserByEmail(request.email());
        if (request.updateEmail() != null) {
            final String newEmail = request.email();
            user.setEmail(newEmail);
            user.setEmailVerified(false);
        }
        if (request.updateFirstName() != null) {
            user.setFirstName(request.updateFirstName());
        }

        if (request.updateLastName() != null) {
            user.setLastName(request.updateLastName());
        }
        user = userRepository.save(user);
        return prepareUserUpdateResponse(user, "User information has been updated!");
    }

    @Override
    public SetRoleResponse setRole(SetRoleRequest request) {
        Role newRole = roleService.getRole(request.role());
        User user = getUserByEmail(request.email());
        user.setRole(newRole);

        Long userId = user.getId();
        String email = user.getEmail();

        user = userRepository.save(user);
        log.info("Role of user with id=#" + userId + " and email='" + email + "' has been updated to " + newRole);

        return SetRoleResponse.builder()
                .id(userId)
                .email(user.getEmail())
                .role(user.getRole().getName())
                .message("User role has been successfully updated!")
                .build();
    }

    @Override
    public UserUpdateResponse updateUserAccountNonExpired(UserUpdateAccountNonExpiredRequest request) {
        User user = getUserByEmail(request.userEmail());
        if (user.isAccountNonExpired() == request.userAccountNonExpired()) {
            throw new StatusAlreadyAppliesException("User account non expired status has already been set to " + user.isAccountNonExpired());
        } else {
            user.setAccountNonExpired(request.userAccountNonExpired());
        }
        user = userRepository.save(user);
        return prepareUserUpdateResponse(user, "User account non expired status has been set to " + user.isAccountNonExpired());
    }

    @Override
    public UserUpdateResponse updateUserAccountNonLocked(UserUpdateAccountNonLockedRequest request) {
        User user = getUserByEmail(request.userEmail());
        if (user.isAccountNonLocked() == request.userAccountNonLocked()) {
            throw new StatusAlreadyAppliesException("User account non locked status has already been set to " + user.isAccountNonLocked());
        } else {
            user.setAccountNonLocked(request.userAccountNonLocked());
        }
        user = userRepository.save(user);
        return prepareUserUpdateResponse(user, "User account non locked status has been set to " + user.isAccountNonLocked());
    }

    @Override
    public UserUpdateResponse updateUserCredentialsNonExpired(UserUpdateCredentialsNonExpiredRequest request) {
        User user = getUserByEmail(request.userEmail());
        if (user.isCredentialsNonExpired() == request.userCredentialsNonExpired()) {
            throw new StatusAlreadyAppliesException("User credentials non expired status has already been set to " + user.isCredentialsNonExpired());
        } else {
            user.setCredentialsNonExpired(request.userCredentialsNonExpired());
        }
        user = userRepository.save(user);
        return prepareUserUpdateResponse(user, "User credentials non expired status has been set to " + user.isCredentialsNonExpired());
    }

    @Override
    public UserUpdateResponse updateUserEnabled(UserUpdateEnabledRequest request) {
        User user = getUserByEmail(request.userEmail());
        if (user.isEnabled() == request.userEnabled()) {
            throw new StatusAlreadyAppliesException("User enabled status has already been set to " + user.isEnabled());
        } else {
            user.setEnabled(request.userEnabled());
        }
        user = userRepository.save(user);
        return prepareUserUpdateResponse(user, "User enabled status has been set to " + user.isEnabled());
    }

    private UserUpdateResponse prepareUserUpdateResponse(User user, String message) {
        return UserUpdateResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .role(user.getRole().getName())
                .message(message)
                .build();
    }

    @Override
    public CustomMessageResponse confirmEmail(String id, String tokenString) {

        User user = getUserById(id);
        List<VerificationToken> tokens = tokenService.getVerificationTokens(user);

        for (VerificationToken token: tokens) {
            if (tokenString.equals(token.getToken()) && !token.isExpired()) {
                user.setEmailVerified(true);
                userRepository.save(user);
                tokenService.deleteVerificationToken(user, token);
                return CustomMessageResponse.builder()
                        .message("Email has been successfully confirmed!")
                        .build();
            }
        }
        log.info("Attempt to verify email failed");
        throw new BadRequestException("Email is not verified!");
    }

    @Override
    public CustomMessageResponse requestPasswordReset(PasswordResetRequest request) {
        User user = getUserByEmail(request.email());
        eventPublisher.publishEvent(new PasswordResetEvent(user));
        return CustomMessageResponse.builder()
                .message("Confirmation request has been sent to " + user.getEmail())
                .build();
    }

    // Once this resetPassword worked, User should provide his/her new password (via frontend)

    @Override
    public PasswordResetResponse resetPassword(String id, String tokenString) {

        User user = getUserById(id);
        List<VerificationToken> tokens = tokenService.getVerificationTokens(user);

        for (VerificationToken token: tokens) {
            if (tokenString.equals(token.getToken()) && !token.isExpired()) {
                return PasswordResetResponse.builder()
                        .message("Please enter your new password!") // frontend should provide a form allowing user to submit a new password
                        .userId(user.getId())
                        .token(tokenString)
                        .build();
            }
        }
        log.info("Attempt to reset password failed");
        throw new BadRequestException("Password is not reset!");
    }

    @Override
    public CustomMessageResponse setNewPassword(SetNewPasswordRequest request) {
        User user = getUserById(request.id());
        List<VerificationToken> tokens = tokenService.getVerificationTokens(user);

        log.debug("List of tokens is empty: " + tokens.isEmpty());

        for (VerificationToken token: tokens) {
            if (request.token().equals(token.getToken()) && !token.isExpired()) {
                user.setPassword(passwordEncoder.encode(request.password()));
                userRepository.save(user);
                tokenService.deleteVerificationToken(user, token);
                
                return CustomMessageResponse.builder()
                        .message("Password has been set!")
                        .build();

            } else if (request.token().equals(token.getToken()) && token.isExpired()) {
                log.debug("Token matched but it has already been expired");
            }
        }
        log.info("Attempt to set a new password failed");
        throw new BadRequestException("A new password is not set!");
    }

}
