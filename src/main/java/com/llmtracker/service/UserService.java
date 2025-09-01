package com.llmtracker.service;

import com.llmtracker.entity.User;
import com.llmtracker.repository.UserRepository;
import com.llmtracker.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByCustomerId(String customerId) {
        return userRepository.findByCustomer_CustomerId(customerId);
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findByUserId(userId);
    }

    public User createUser(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode("defaultPassword123"));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        if (user.getActive() == null) {
            user.setActive(true);
        }

        if (user.getCustomerId() != null && user.getCustomer() == null) {
            customerRepository.findByCustomerId(user.getCustomerId()).ifPresent(user::setCustomer);
        }

        return userRepository.save(user);
    }

    public User updateUser(String userId, User userDetails) {
        Optional<User> existingUser = userRepository.findByUserId(userId);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            
            if (userDetails.getFirstName() != null) {
                user.setFirstName(userDetails.getFirstName());
            }
            if (userDetails.getLastName() != null) {
                user.setLastName(userDetails.getLastName());
            }
            if (userDetails.getEmail() != null) {
                user.setEmail(userDetails.getEmail());
            }
            if (userDetails.getDepartment() != null) {
                user.setDepartment(userDetails.getDepartment());
            }
            if (userDetails.getRole() != null) {
                user.setRole(userDetails.getRole());
            }
            if (userDetails.getActive() != null) {
                user.setActive(userDetails.getActive());
            }
            if (userDetails.getCustomerId() != null) {
                customerRepository.findByCustomerId(userDetails.getCustomerId()).ifPresent(user::setCustomer);
            }
            
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with userId: " + userId);
    }

    public void deleteUser(String userId) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            throw new RuntimeException("User not found with userId: " + userId);
        }
    }

    public List<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm);
    }

    public List<User> searchUsersByCustomerId(String searchTerm, String customerId) {
        return userRepository.searchUsersByCustomer(searchTerm, customerId);
    }

    public boolean isUserInCustomer(String userId, String customerId) {
        Optional<User> user = userRepository.findByUserId(userId);
        return user.isPresent() && customerId.equals(user.get().getCustomerId());
    }
}
