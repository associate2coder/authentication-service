package ua.com.associate2coder.authenticationservice.users.services;

import ua.com.associate2coder.authenticationservice.authentication.UserRegistrationRequest;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.users.dto.*;

public interface UserService {


    User createUser(UserRegistrationRequest request);

    User getUserByEmail(String email);

    User getUserById(Long id);

    UserDeleteResponse deleteUser(UserDeleteRequest request);
    UserUpdateResponse updateUser(UserUpdateRequest request);
    SetRoleResponse setRole(SetRoleRequest request);
    UserUpdateResponse updateUserAccountNonExpired(UserUpdateAccountNonExpiredRequest userUpdateEnabledStatusRequest);
    UserUpdateResponse updateUserAccountNonLocked(UserUpdateAccountNonLockedRequest userUpdateEnabledStatusRequest);
    UserUpdateResponse updateUserCredentialsNonExpired(UserUpdateCredentialsNonExpiredRequest userUpdateEnabledStatusRequest);
    UserUpdateResponse updateUserEnabled(UserUpdateEnabledRequest userUpdateEnabledRequest);

    CustomMessageResponse confirmEmail(String id, String token);

    CustomMessageResponse requestPasswordReset(PasswordResetRequest request);

    PasswordResetResponse resetPassword(String id, String token);

    CustomMessageResponse setNewPassword(SetNewPasswordRequest request);

}
