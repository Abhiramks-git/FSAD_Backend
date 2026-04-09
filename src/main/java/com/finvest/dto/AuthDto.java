package com.finvest.dto;

public class AuthDto {

    // ── Login ─────────────────────────────────────────────────────────────────
    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail()              { return email; }
        public void   setEmail(String email)  { this.email = email; }
        public String getPassword()               { return password; }
        public void   setPassword(String password){ this.password = password; }
    }

    // ── Register ──────────────────────────────────────────────────────────────
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
        public String getName()                   { return name; }
        public void   setName(String name)        { this.name = name; }
        public String getEmail()                  { return email; }
        public void   setEmail(String email)      { this.email = email; }
        public String getPassword()               { return password; }
        public void   setPassword(String password){ this.password = password; }
    }

    // ── Forgot password ───────────────────────────────────────────────────────
    public static class ForgotPasswordRequest {
        private String email;
        public String getEmail()             { return email; }
        public void   setEmail(String email) { this.email = email; }
    }

    // ── Verify OTP ────────────────────────────────────────────────────────────
    public static class VerifyOtpRequest {
        private String email;
        private String token;
        public String getEmail()             { return email; }
        public void   setEmail(String email) { this.email = email; }
        public String getToken()             { return token; }
        public void   setToken(String token) { this.token = token; }
    }

    // ── Reset password ────────────────────────────────────────────────────────
    public static class ResetPasswordRequest {
        private String email;
        private String token;
        private String newPassword;
        public String getEmail()                      { return email; }
        public void   setEmail(String email)          { this.email = email; }
        public String getToken()                      { return token; }
        public void   setToken(String token)          { this.token = token; }
        public String getNewPassword()                { return newPassword; }
        public void   setNewPassword(String v)        { this.newPassword = v; }
    }

    // ── Social login ──────────────────────────────────────────────────────────
    public static class SocialLoginRequest {
        private String provider;
        private String email;
        private String name;
        private String password;
        public String getProvider()               { return provider; }
        public void   setProvider(String v)       { this.provider = v; }
        public String getEmail()                  { return email; }
        public void   setEmail(String v)          { this.email = v; }
        public String getName()                   { return name; }
        public void   setName(String v)           { this.name = v; }
        public String getPassword()               { return password; }
        public void   setPassword(String v)       { this.password = v; }
    }

    // ── Auth response (returned on successful login/register) ─────────────────
    public static class AuthResponse {
        private String token;
        private Long   id;
        private String name;
        private String email;
        private String role;

        public AuthResponse() {}
        public AuthResponse(String token, Long id, String name, String email, String role) {
            this.token = token; this.id = id; this.name = name;
            this.email = email; this.role = role;
        }
        public String getToken()           { return token; }
        public void   setToken(String v)   { this.token = v; }
        public Long   getId()              { return id; }
        public void   setId(Long v)        { this.id = v; }
        public String getName()            { return name; }
        public void   setName(String v)    { this.name = v; }
        public String getEmail()           { return email; }
        public void   setEmail(String v)   { this.email = v; }
        public String getRole()            { return role; }
        public void   setRole(String v)    { this.role = v; }
    }

    // ── Generic ok/message response ───────────────────────────────────────────
    public static class MessageResponse {
        private boolean ok;
        private String  message;

        public MessageResponse() {}
        public MessageResponse(boolean ok, String message) {
            this.ok = ok; this.message = message;
        }
        public boolean isOk()                 { return ok; }
        public void    setOk(boolean ok)      { this.ok = ok; }
        public String  getMessage()           { return message; }
        public void    setMessage(String v)   { this.message = v; }
    }
}
