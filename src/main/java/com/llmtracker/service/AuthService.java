package com.llmtracker.service;

import com.llmtracker.dto.AuthRequest;
import com.llmtracker.dto.AuthResponse;
import com.llmtracker.entity.User;
import com.llmtracker.repository.UserRepository;
import com.llmtracker.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    public AuthResponse authenticate(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            
            String token = jwtService.generateToken(
                userDetails.getUsername(),
                customUserDetails.getRole(),
                customUserDetails.getCustomerId()
            );
            
            System.out.println("User authenticated: " + customUserDetails.getUsername());
            System.out.println("User role: " + customUserDetails.getRole());
            System.out.println("User customer: " + customUserDetails.getCustomerId());

            return new AuthResponse(
                token,
                customUserDetails.getUserId(),
                customUserDetails.getUsername(),
                customUserDetails.getFullName(),
                customUserDetails.getRole(),
                customUserDetails.getCustomerId(),
                customUserDetails.getCustomerName(),
                jwtService.getExpirationTime()
            );

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("User not found");
        }
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
