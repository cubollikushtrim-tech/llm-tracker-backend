package com.llmtracker.controller;

import com.llmtracker.dto.AnalyticsResponse;
import com.llmtracker.service.UsageEventService;
import com.llmtracker.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "API for analytics and reporting")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {

    @Autowired
    private UsageEventService usageEventService;

    @GetMapping("/usage")
    @Operation(summary = "Get usage analytics", description = "Get comprehensive usage analytics with breakdowns")
    public ResponseEntity<AnalyticsResponse> getUsageAnalytics(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String vendor) {
        
        String currentUserCustomerId = getCurrentUserCustomerId();
        
        customerId = resolveCustomerId(customerId, currentUserCustomerId);
        
        if (!canAccessCustomerData(customerId, currentUserCustomerId)) {
            return ResponseEntity.status(403).build();
        }
        
        AnalyticsResponse response = usageEventService.getAnalytics(period, startDate, endDate, customerId, userId, vendor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/costs")
    @Operation(summary = "Get cost analytics", description = "Get cost breakdown and analysis")
    public ResponseEntity<AnalyticsResponse> getCostAnalytics(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String vendor) {
        
        String currentUserCustomerId = getCurrentUserCustomerId();
        
        customerId = resolveCustomerId(customerId, currentUserCustomerId);
        
        if (!canAccessCustomerData(customerId, currentUserCustomerId)) {
            return ResponseEntity.status(403).build();
        }
        
        AnalyticsResponse response = usageEventService.getAnalytics(period, startDate, endDate, customerId, userId, vendor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue analytics", description = "Get revenue and profitability analysis")
    public ResponseEntity<AnalyticsResponse> getRevenueAnalytics(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String vendor) {
        
        String currentUserCustomerId = getCurrentUserCustomerId();
        
        customerId = resolveCustomerId(customerId, currentUserCustomerId);
        
        if (!canAccessCustomerData(customerId, currentUserCustomerId)) {
            return ResponseEntity.status(403).build();
        }
        
        AnalyticsResponse response = usageEventService.getAnalytics(period, startDate, endDate, customerId, userId, vendor);
        return ResponseEntity.ok(response);
    }
    
    private String getCurrentUserCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getCustomerId();
        }
        return null;
    }
    
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return "ADMIN".equals(userDetails.getRole());
        }
        return false;
    }
    
    private boolean isCurrentUserSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return "SUPERADMIN".equals(userDetails.getRole());
        }
        return false;
    }
    
    private String resolveCustomerId(String requestedCustomerId, String currentUserCustomerId) {
        System.out.println("DEBUG: resolveCustomerId - requestedCustomerId: " + requestedCustomerId + 
                          ", currentUserCustomerId: " + currentUserCustomerId + 
                          ", isSuperAdmin: " + isCurrentUserSuperAdmin());
        
        if (requestedCustomerId == null || requestedCustomerId.isEmpty()) {
            if (isCurrentUserSuperAdmin()) {
                System.out.println("DEBUG: SUPERADMIN - returning null (no filtering)");
                return null;
            } else {
                System.out.println("DEBUG: Regular user - returning currentUserCustomerId: " + currentUserCustomerId);
                return currentUserCustomerId;
            }
        }
        System.out.println("DEBUG: Returning requestedCustomerId: " + requestedCustomerId);
        return requestedCustomerId;
    }
    
    private boolean canAccessCustomerData(String requestedCustomerId, String currentUserCustomerId) {
        if (isCurrentUserSuperAdmin()) {
            System.out.println("DEBUG: SUPERADMIN access granted - can access all data");
            return true;
        }
        
        boolean canAccess = requestedCustomerId == null || requestedCustomerId.equals(currentUserCustomerId);
        System.out.println("DEBUG: User access check - requestedCustomerId: " + requestedCustomerId + 
                          ", currentUserCustomerId: " + currentUserCustomerId + 
                          ", canAccess: " + canAccess);
        return canAccess;
    }
}
