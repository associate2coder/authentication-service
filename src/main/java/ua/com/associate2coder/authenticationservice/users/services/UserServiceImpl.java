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
import ua.com.associate2coder.authenticationservice.users.repositories.UserRepository;

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
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ElementNotFoundException("User not found with email: " + email));
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("User not found with id #: " + id));
    }

    @Override
    public UserDeleteResponse deleteUser(UserDeleteRequest request) {
        String email = request.email();
        User user = getUser(email);
        userRepository.delete(user);
        return UserDeleteResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .message("User has been deleted.")
                .build();
    }

    @Override
    public UserUpdateResponse updateUser(UserUpdateRequest request) {
        User user = getUser(request.email());
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
        User user = getUser(request.email());
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
        User user = getUser(request.userEmail());
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
        User user = getUser(request.userEmail());
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
        User user = getUser(request.userEmail());
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
        User user = getUser(request.userEmail());
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
    public EmailConfirmationResponse confirmEmail(String id, String tokenString) {

        long userId;
        try {
            userId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid id number");
        }

        User user = getUser(userId);
        VerificationToken token = tokenService.getVerificationToken(user);
        System.out.println(token.getToken() + " --> TOKEN IN DB");
        System.out.println(tokenString + " --> TOKEN FROM REQUEST");
        if (tokenString.equals(token.getToken())) {
            user.setEmailVerified(true);
        } else {
            throw new BadRequestException("Email is not verified!");
        }
        userRepository.save(user);
        tokenService.deleteVerificationTokens(user);
        return new EmailConfirmationResponse("Email has been successfully confirmed!");
    }

}
