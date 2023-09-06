package ua.com.associate2coder.authenticationservice.users.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ua.com.associate2coder.authenticationservice.users.dto.EmailConfirmationResponse;
import ua.com.associate2coder.authenticationservice.users.services.UserService;

@RestController
@RequiredArgsConstructor
public class EmailVerificationController {

    private final UserService userService;
    @GetMapping("/api/v1/email/confirmation/{id}/{token}")
    public ResponseEntity<EmailConfirmationResponse> confirmEmail(@PathVariable String id, @PathVariable String token) {
        return ResponseEntity.ok(userService.confirmEmail(id, token));
    }
}
