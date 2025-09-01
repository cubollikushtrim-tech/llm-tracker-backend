package com.llmtracker.controller;

import com.llmtracker.dto.AuthRequest;
import com.llmtracker.dto.AuthResponse;
import com.llmtracker.service.AuthService;
import com.llmtracker.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for user authentication")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get information about the currently authenticated user")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            AuthResponse response = new AuthResponse(
                null,
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getFullName(),
                userDetails.getRole(),
                userDetails.getCustomerId(),
                userDetails.getCustomerName(),
                null 
            );
            
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user (client should discard token)")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}
