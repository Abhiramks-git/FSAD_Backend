package com.finvest.controller;

import com.finvest.dto.AuthDto.AuthResponse;
import com.finvest.dto.AuthDto.ForgotPasswordRequest;
import com.finvest.dto.AuthDto.LoginRequest;
import com.finvest.dto.AuthDto.MessageResponse;
import com.finvest.dto.AuthDto.RegisterRequest;
import com.finvest.dto.AuthDto.ResetPasswordRequest;
import com.finvest.dto.AuthDto.SocialLoginRequest;
import com.finvest.dto.AuthDto.VerifyOtpRequest;
import com.finvest.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            AuthResponse res = authService.login(req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse(false, "Invalid email or password."));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/social-login")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequest req) {
        try {
            return ResponseEntity.ok(authService.socialLogin(req));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        return ResponseEntity.ok(authService.requestPasswordReset(req));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest req) {
        return ResponseEntity.ok(authService.verifyOtp(req));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        return ResponseEntity.ok(authService.resetPassword(req));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(authService.getProfile(ud.getUsername()));
    }
}
