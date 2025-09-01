package com.llmtracker.repository;

import com.llmtracker.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerId(String customerId);
    
    List<Customer> findByActiveTrue();
    
    boolean existsByCustomerId(String customerId);
    
    @Query("SELECT c FROM Customer c WHERE c.organizationName LIKE %:searchTerm% OR c.contactEmail LIKE %:searchTerm%")
    List<Customer> searchCustomers(@Param("searchTerm") String searchTerm);
}
