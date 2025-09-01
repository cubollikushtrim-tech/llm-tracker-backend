package com.llmtracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.llmtracker.repository.CustomerRepository;
import com.llmtracker.repository.UsageEventRepository;
import com.llmtracker.repository.UserRepository;
import com.llmtracker.service.DataInitializationService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsageEventRepository usageEventRepository;

    @Autowired
    private DataInitializationService dataInitializationService;

    @PostMapping("/add-random-events")
    public ResponseEntity<String> addRandomEvents(@RequestBody Map<String, Object> payload) {
        int count = 100;
        boolean todayOnly = false;
        if (payload != null) {
            if (payload.get("count") != null) {
                Object countObj = payload.get("count");
                if (countObj instanceof Number) {
                    count = ((Number) countObj).intValue();
                } else {
                    try {
                        count = Integer.parseInt(countObj.toString());
                    } catch (Exception ignored) {}
                }
            }
            if (payload.get("todayOnly") != null) {
                Object todayObj = payload.get("todayOnly");
                if (todayObj instanceof Boolean) {
                    todayOnly = (Boolean) todayObj;
                } else {
                    todayOnly = Boolean.parseBoolean(todayObj.toString());
                }
            }
        }
        dataInitializationService.addRandomEvents(count, todayOnly);
        return ResponseEntity.ok("Added " + count + " random events");
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("timestamp", java.time.LocalDateTime.now());
        try {
            long customerCount = customerRepository.count();
            long userCount = userRepository.count();
            long eventCount = usageEventRepository.count();
            response.put("customers", customerCount);
            response.put("users", userCount);
            response.put("events", eventCount);
            response.put("database", "Connected");
        } catch (Exception e) {
            response.put("database", "Error: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insert-sample-data")
    public ResponseEntity<Map<String, Object>> insertSampleData() {
        Map<String, Object> response = new HashMap<>();
        try {
            Long customer1Id = customerRepository.findByCustomerId("cust_001").get().getId();
            Long customer2Id = customerRepository.findByCustomerId("cust_002").get().getId();
            Long customer3Id = customerRepository.findByCustomerId("cust_003").get().getId();
            Long user1Id = userRepository.findByUserId("user_001").get().getId();
            Long user2Id = userRepository.findByUserId("user_002").get().getId();
            Long user3Id = userRepository.findByUserId("user_003").get().getId();
            Long user4Id = userRepository.findByUserId("user_004").get().getId();
            response.put("message", "Sample data insertion attempted");
            response.put("customer1Id", customer1Id);
            response.put("customer2Id", customer2Id);
            response.put("customer3Id", customer3Id);
            response.put("user1Id", user1Id);
            response.put("user2Id", user2Id);
            response.put("user3Id", user3Id);
            response.put("user4Id", user4Id);
            response.put("status", "SUCCESS");
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "ERROR");
        }
        return ResponseEntity.ok(response);
    }
}
