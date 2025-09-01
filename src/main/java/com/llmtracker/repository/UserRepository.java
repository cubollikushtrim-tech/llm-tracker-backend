package com.llmtracker.repository;

import com.llmtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);
    
    List<User> findByCustomer_CustomerId(String customerId);
    
    List<User> findByActiveTrue();
    
    boolean existsByUserId(String userId);
    
    @Query("SELECT u FROM User u WHERE u.customer.customerId = :customerId AND u.active = true")
    List<User> findActiveUsersByCustomerId(@Param("customerId") String customerId);
    
    @Query("SELECT u FROM User u WHERE (u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.email LIKE %:searchTerm%) AND u.customer.customerId = :customerId")
    List<User> searchUsersByCustomer(@Param("searchTerm") String searchTerm, @Param("customerId") String customerId);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndActiveTrue(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.customer WHERE u.email = :email AND u.active = true")
    Optional<User> findByEmailAndActiveTrueWithCustomer(@Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.email LIKE %:searchTerm%")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT u FROM User u WHERE (u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.email LIKE %:searchTerm%) AND u.customer.customerId = :customerId")
    List<User> searchUsersByCustomerId(@Param("searchTerm") String searchTerm, @Param("customerId") String customerId);
}
