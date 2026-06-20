package com.customer360.backend.controller;

import com.customer360.backend.dto.AuthRequest;
import com.customer360.backend.dto.AuthResponse;
import com.customer360.backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final Map<String, DemoUser> users = new HashMap<>();

    public AuthController(
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.jwtService = jwtService;

        users.put(
                "admin",
                new DemoUser(
                        "admin",
                        passwordEncoder.encode("Admin@123"),
                        "Admin User",
                        "ADMIN"
                )
        );

        users.put(
                "manager",
                new DemoUser(
                        "manager",
                        passwordEncoder.encode("Manager@123"),
                        "Manager User",
                        "MANAGER"
                )
        );

        users.put(
                "viewer",
                new DemoUser(
                        "viewer",
                        passwordEncoder.encode("Viewer@123"),
                        "Viewer User",
                        "VIEWER"
                )
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username and password are required"
            );
        }

        String username = request.getUsername().trim().toLowerCase();
        DemoUser user = users.get(username);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        PasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        if (!passwordEncoder.matches(request.getPassword(), user.password())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        String token = jwtService.generateToken(
                user.username(),
                user.fullName(),
                user.role()
        );

        return new AuthResponse(
                token,
                user.username(),
                user.fullName(),
                user.role()
        );
    }

    @GetMapping("/me")
    public AuthResponse me() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not authenticated"
            );
        }

        String username = String.valueOf(authentication.getPrincipal());
        DemoUser user = users.get(username);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found"
            );
        }

        return new AuthResponse(
                null,
                user.username(),
                user.fullName(),
                user.role()
        );
    }

    private record DemoUser(
            String username,
            String password,
            String fullName,
            String role
    ) {
    }
}
