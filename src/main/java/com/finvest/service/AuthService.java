package com.finvest.service;

import com.finvest.dto.AuthDto.AuthResponse;
import com.finvest.dto.AuthDto.ForgotPasswordRequest;
import com.finvest.dto.AuthDto.LoginRequest;
import com.finvest.dto.AuthDto.MessageResponse;
import com.finvest.dto.AuthDto.RegisterRequest;
import com.finvest.dto.AuthDto.ResetPasswordRequest;
import com.finvest.dto.AuthDto.SocialLoginRequest;
import com.finvest.dto.AuthDto.VerifyOtpRequest;
import com.finvest.entity.User;
import com.finvest.entity.User.Role;
import com.finvest.repository.UserRepository;
import com.finvest.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@Service
@Transactional
public class AuthService {

    private static final Logger log = Logger.getLogger(AuthService.class.getName());

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    // ── Login ──────────────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepo.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthResponse(token, user.getId(), user.getName(),
                user.getEmail(), user.getRole().name());
    }

    // ── Register ───────────────────────────────────────────────────────────────
    public MessageResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail().toLowerCase())) {
            return new MessageResponse(false, "An account with this email already exists.");
        }
        User user = new User(req.getName(), req.getEmail().toLowerCase(),
                encoder.encode(req.getPassword()), Role.investor);
        userRepo.save(user);
        return new MessageResponse(true, "Account created successfully. Please sign in.");
    }

    // ── Social login ───────────────────────────────────────────────────────────
    public AuthResponse socialLogin(SocialLoginRequest req) {
        Optional<User> existing = userRepo.findByEmail(req.getEmail().toLowerCase());
        User user;
        if (existing.isPresent()) {
            user = existing.get();
            if (!encoder.matches(req.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Wrong password for this email.");
            }
        } else {
            String displayName = (req.getName() != null && !req.getName().isEmpty())
                    ? req.getName() : req.getEmail().split("@")[0];
            user = new User(displayName, req.getEmail().toLowerCase(),
                    encoder.encode(req.getPassword()), Role.investor);
            userRepo.save(user);
        }
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthResponse(token, user.getId(), user.getName(),
                user.getEmail(), user.getRole().name());
    }

    // ── Forgot Password Step 1: send real email OTP ────────────────────────────
    public MessageResponse requestPasswordReset(ForgotPasswordRequest req) {
        User user = userRepo.findByEmail(req.getEmail().toLowerCase()).orElse(null);
        if (user == null) {
            // Don't reveal whether the email exists
            return new MessageResponse(true,
                    "If an account with that email exists, an OTP has been sent.");
        }
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setResetToken(otp);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userRepo.save(user);

        // Sends via Gmail SMTP asynchronously
        emailService.sendOtpEmail(user.getEmail(), user.getName(), otp);
        log.info("Password reset OTP generated for " + req.getEmail());

        return new MessageResponse(true,
                "OTP sent to " + req.getEmail() + ". Check your inbox and spam folder.");
    }

    // ── Forgot Password Step 2: verify OTP ────────────────────────────────────
    public MessageResponse verifyOtp(VerifyOtpRequest req) {
        User user = userRepo.findByEmail(req.getEmail().toLowerCase()).orElse(null);
        if (user == null || user.getResetToken() == null) {
            return new MessageResponse(false, "No reset request found. Please request a new OTP.");
        }
        if (LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            return new MessageResponse(false, "OTP expired. Please request a new one.");
        }
        if (!user.getResetToken().equals(req.getToken())) {
            return new MessageResponse(false, "Incorrect OTP. Please check your email and try again.");
        }
        return new MessageResponse(true, "OTP verified.");
    }

    // ── Forgot Password Step 3: set new password ───────────────────────────────
    public MessageResponse resetPassword(ResetPasswordRequest req) {
        VerifyOtpRequest vr = new VerifyOtpRequest();
        vr.setEmail(req.getEmail());
        vr.setToken(req.getToken());
        MessageResponse verify = verifyOtp(vr);
        if (!verify.isOk()) return verify;

        User user = userRepo.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(encoder.encode(req.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepo.save(user);
        return new MessageResponse(true, "Password reset successfully. You can now sign in.");
    }

    // ── Get profile ────────────────────────────────────────────────────────────
    public AuthResponse getProfile(String email) {
        User user = userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new AuthResponse(null, user.getId(), user.getName(),
                user.getEmail(), user.getRole().name());
    }
}
