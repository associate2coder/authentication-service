package ua.com.associate2coder.authenticationservice.users.services;

import ua.com.associate2coder.authenticationservice.authentication.UserRegistrationRequest;
import ua.com.associate2coder.authenticationservice.entities.User;
import ua.com.associate2coder.authenticationservice.users.dto.*;

public interface UserService {


    User createUser(UserRegistrationRequest request);

    User getUserByEmail(String email);

    User getUserById(Long id);
    User getUserById(String id);

    UserDeleteResponse deleteUser(UserDeleteRequest request);
    UserUpdateResponse updateUser(UserUpdateRequest request);

    CustomMessageResponse confirmEmail(String id, String token);

    CustomMessageResponse setNewPassword(SetNewPasswordRequest request);

}
