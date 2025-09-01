package com.llmtracker.controller;

import com.llmtracker.dto.UsageEventRequest;
import com.llmtracker.dto.UsageEventResponse;
import com.llmtracker.service.UsageEventService;
import com.llmtracker.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Usage Events", description = "API for managing LLM usage events")
@CrossOrigin(origins = "http://localhost:3000")
public class UsageEventController {

    @Autowired
    private UsageEventService usageEventService;

    @PostMapping
    @Operation(summary = "Submit a usage event", description = "Submit a new LLM usage event with cost calculation")
    public ResponseEntity<UsageEventResponse> submitEvent(@Valid @RequestBody UsageEventRequest request) {
        UsageEventResponse response = usageEventService.processUsageEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get usage events", description = "Get usage events with optional filters")
    public ResponseEntity<Page<UsageEventResponse>> getEvents(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String vendor,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String apiType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        String currentUserCustomerId = getCurrentUserCustomerId();
        
        customerId = resolveCustomerId(customerId, currentUserCustomerId);
        
        if (!canAccessCustomerData(customerId, currentUserCustomerId)) {
            return ResponseEntity.status(403).build();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UsageEventResponse> events = usageEventService.getUsageEvents(
            customerId, userId, vendor, model, apiType, startDate, endDate, pageable);
        
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get event by ID", description = "Get a specific usage event by its ID")
    public ResponseEntity<UsageEventResponse> getEventById(@PathVariable String eventId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
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
