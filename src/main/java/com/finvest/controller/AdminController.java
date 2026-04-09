package com.finvest.controller;

import com.finvest.entity.User;
import com.finvest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('admin')")
public class AdminController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new MessageSimple(false, "Email is required"));
        }
        if (userRepo.existsByEmail(email.toLowerCase())) {
            return ResponseEntity.badRequest()
                    .body(new MessageSimple(false, "Email already exists"));
        }
        String roleName = body.getOrDefault("role", "investor");
        User.Role role;
        try {
            role = User.Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            role = User.Role.investor;
        }
        User u = new User(body.get("name"), email.toLowerCase(),
                encoder.encode(body.get("password")), role);
        return ResponseEntity.ok(userRepo.save(u));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (body.containsKey("name") && body.get("name") != null) {
            u.setName(body.get("name"));
        }
        if (body.containsKey("email") && body.get("email") != null) {
            u.setEmail(body.get("email").toLowerCase());
        }
        if (body.containsKey("role") && body.get("role") != null) {
            try {
                u.setRole(User.Role.valueOf(body.get("role")));
            } catch (IllegalArgumentException ignored) {}
        }
        if (body.containsKey("password") && body.get("password") != null
                && !body.get("password").isBlank()) {
            u.setPassword(encoder.encode(body.get("password")));
        }
        return ResponseEntity.ok(userRepo.save(u));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        Map<String, Object> resp = new HashMap<>();
        resp.put("ok", true);
        return ResponseEntity.ok(resp);
    }

    // Simple inner class to avoid Lombok dependency
    static class MessageSimple {
        private boolean ok;
        private String message;
        MessageSimple(boolean ok, String message) { this.ok = ok; this.message = message; }
        public boolean isOk()       { return ok; }
        public String getMessage()  { return message; }
    }
}
