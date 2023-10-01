package ua.com.associate2coder.authenticationservice.users.services;

import ua.com.associate2coder.authenticationservice.users.dto.*;

public interface UserAttributeUpdaterService {

    SetRoleResponse setRole(SetRoleRequest request);
    UserUpdateResponse updateUserAccountNonExpired(UserUpdateAccountNonExpiredRequest userUpdateEnabledStatusRequest);
    UserUpdateResponse updateUserAccountNonLocked(UserUpdateAccountNonLockedRequest userUpdateEnabledStatusRequest);
    UserUpdateResponse updateUserCredentialsNonExpired(UserUpdateCredentialsNonExpiredRequest userUpdateEnabledStatusRequest);
    UserUpdateResponse updateUserEnabled(UserUpdateEnabledRequest userUpdateEnabledRequest);

}
