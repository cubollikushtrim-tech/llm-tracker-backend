package com.llmtracker.controller;

import com.llmtracker.entity.Customer;
import com.llmtracker.repository.CustomerRepository;
import com.llmtracker.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "API for customer management")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    @Operation(summary = "Get all customers", description = "Get a list of all active customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        if (!isCurrentUserSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        List<Customer> customers = customerRepository.findByActiveTrue();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID", description = "Get a specific customer by their customer ID")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String customerId) {
        if (!isCurrentUserSuperAdmin() && !isCurrentUserCustomer(customerId)) {
            return ResponseEntity.status(403).build();
        }
        
        Optional<Customer> customer = customerRepository.findByCustomerId(customerId);
        return customer.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create customer", description = "Create a new customer")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        if (!isCurrentUserSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        if (customerRepository.existsByCustomerId(customer.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer", description = "Update an existing customer")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String customerId, 
                                                 @Valid @RequestBody Customer customerDetails) {
        if (!isCurrentUserSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Optional<Customer> existingCustomer = customerRepository.findByCustomerId(customerId);
        
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            customer.setOrganizationName(customerDetails.getOrganizationName());
            customer.setContactEmail(customerDetails.getContactEmail());
            customer.setContactPhone(customerDetails.getContactPhone());
            customer.setAddress(customerDetails.getAddress());
            customer.setMarkupPercentage(customerDetails.getMarkupPercentage());
            customer.setActive(customerDetails.getActive());
            
            Customer updatedCustomer = customerRepository.save(customer);
            return ResponseEntity.ok(updatedCustomer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete customer", description = "Soft delete a customer by setting active to false")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        if (!isCurrentUserSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Optional<Customer> customer = customerRepository.findByCustomerId(customerId);
        
        if (customer.isPresent()) {
            Customer c = customer.get();
            c.setActive(false);
            customerRepository.save(c);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers", description = "Search customers by organization name or email")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String searchTerm) {
        if (!isCurrentUserSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        List<Customer> customers = customerRepository.searchCustomers(searchTerm);
        return ResponseEntity.ok(customers);
    }
    
    private boolean isCurrentUserSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return "SUPERADMIN".equals(userDetails.getRole());
        }
        return false;
    }
    
    private boolean isCurrentUserCustomer(String customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userCustomerId = userDetails.getCustomerId();
            return userCustomerId != null && customerId.equals(userCustomerId);
        }
        return false;
    }
}
