package ua.com.associate2coder.authenticationservice.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.associate2coder.authenticationservice.users.dto.CustomMessageResponse;
import ua.com.associate2coder.authenticationservice.users.dto.PasswordResetRequest;
import ua.com.associate2coder.authenticationservice.users.dto.PasswordResetResponse;
import ua.com.associate2coder.authenticationservice.users.dto.SetNewPasswordRequest;
import ua.com.associate2coder.authenticationservice.users.services.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/password")
public class PasswordResetController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;
    @PostMapping("/reset")
    public ResponseEntity<CustomMessageResponse> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(passwordResetService.requestPasswordReset(request));
    }

    @GetMapping("/reset/{id}/{token}")
    public ResponseEntity<PasswordResetResponse> resetPassword(@PathVariable String id, @PathVariable String token) {
        return ResponseEntity.ok(passwordResetService.resetPassword(id, token));
    }

    @PutMapping("/new")
    public ResponseEntity<CustomMessageResponse> setNewPassword(@RequestBody SetNewPasswordRequest request) {
        return ResponseEntity.ok(userService.setNewPassword(request));
    }
}
