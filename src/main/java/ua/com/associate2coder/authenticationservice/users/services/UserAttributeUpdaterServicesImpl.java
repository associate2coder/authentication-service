package ua.com.associate2coder.authenticationservice.users.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.com.associate2coder.authenticationservice.common.exceptions.StatusAlreadyAppliesException;
import ua.com.associate2coder.authenticationservice.entities.Role;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.users.dto.*;
import ua.com.associate2coder.authenticationservice.users.repositories.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAttributeUpdaterServicesImpl implements UserAttributeUpdaterService {

    private final RoleService roleService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public SetRoleResponse setRole(SetRoleRequest request) {
        Role newRole = roleService.getRole(request.role());
        User user = userService.getUserByEmail(request.email());
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
        User user = userService.getUserByEmail(request.userEmail());
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
        User user = userService.getUserByEmail(request.userEmail());
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
        User user = userService.getUserByEmail(request.userEmail());
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
        User user = userService.getUserByEmail(request.userEmail());
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
}
