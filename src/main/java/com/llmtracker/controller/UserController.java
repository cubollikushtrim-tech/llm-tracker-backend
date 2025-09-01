package com.llmtracker.controller;

import com.llmtracker.entity.User;
import com.llmtracker.service.UserService;
import com.llmtracker.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }

        if (isCurrentUserSuperAdmin()) {
            return ResponseEntity.ok(userService.getAllUsers());
        }

        if (isCurrentUserAdmin()) {
            String customerId = userDetails.getCustomerId();
            if (customerId != null) {
                return ResponseEntity.ok(userService.getUsersByCustomerId(customerId));
            }
        }

        return ResponseEntity.status(403).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        CustomUserDetails userDetails = getCurrentUserDetails();
        
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }

        if (isCurrentUserSuperAdmin()) {
            return userService.getUserById(userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        if (isCurrentUserAdmin()) {
            String customerId = userDetails.getCustomerId();
            if (customerId != null && canAccessUserData(userId, customerId)) {
                return userService.getUserById(userId)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
            }
        }

        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        CustomUserDetails userDetails = getCurrentUserDetails();
        
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }

        if (!isCurrentUserAdmin() && !isCurrentUserSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }

        if (isCurrentUserAdmin() && !isCurrentUserSuperAdmin()) {
            String customerId = userDetails.getCustomerId();
            if (customerId != null) {
                user.setCustomerId(customerId);
            } else {
                return ResponseEntity.status(403).build();
            }
        }

        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User user) {
        CustomUserDetails userDetails = getCurrentUserDetails();
        
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }

        if (!isCurrentUserAdmin() && !isCurrentUserSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }

        if (isCurrentUserAdmin() && !isCurrentUserSuperAdmin()) {
            String customerId = userDetails.getCustomerId();
            if (customerId == null || !canAccessUserData(userId, customerId)) {
                return ResponseEntity.status(403).build();
            }
        }

        try {
            User updatedUser = userService.updateUser(userId, user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        CustomUserDetails userDetails = getCurrentUserDetails();
        
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }

        if (!isCurrentUserAdmin() && !isCurrentUserSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }

        if (isCurrentUserAdmin() && !isCurrentUserSuperAdmin()) {
            String customerId = userDetails.getCustomerId();
            if (customerId == null || !canAccessUserData(userId, customerId)) {
                return ResponseEntity.status(403).build();
            }
        }

        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String searchTerm) {
        CustomUserDetails userDetails = getCurrentUserDetails();
        
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }

        if (isCurrentUserSuperAdmin()) {
            return ResponseEntity.ok(userService.searchUsers(searchTerm));
        }

        if (isCurrentUserAdmin()) {
            String customerId = userDetails.getCustomerId();
            if (customerId != null) {
                return ResponseEntity.ok(userService.searchUsersByCustomerId(searchTerm, customerId));
            }
        }

        return ResponseEntity.status(403).build();
    }

    private CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    private boolean isCurrentUserSuperAdmin() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null && "SUPERADMIN".equals(userDetails.getRole());
    }

    private boolean isCurrentUserAdmin() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null && ("ADMIN".equals(userDetails.getRole()) || "SUPERADMIN".equals(userDetails.getRole()));
    }

    private boolean canAccessUserData(String userId, String customerId) {
        return userService.isUserInCustomer(userId, customerId);
    }
}
