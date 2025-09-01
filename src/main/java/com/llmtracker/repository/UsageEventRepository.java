package com.llmtracker.repository;

import com.llmtracker.entity.UsageEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface UsageEventRepository extends JpaRepository<UsageEvent, Long> {

    List<UsageEvent> findByCustomerCustomerId(String customerId);
    
    List<UsageEvent> findByUser_UserId(String userId);
    
    List<UsageEvent> findByVendor(String vendor);
    
    List<UsageEvent> findByModel(String model);
    
    List<UsageEvent> findByApiType(String apiType);
    
    List<UsageEvent> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT ue FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate")
    List<UsageEvent> findByCustomerAndDateRange(@Param("customerId") String customerId, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue FROM UsageEvent ue WHERE ue.user.userId = :userId AND ue.timestamp BETWEEN :startDate AND :endDate")
    List<UsageEvent> findByUserAndDateRange(@Param("userId") String userId, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue FROM UsageEvent ue WHERE ue.vendor = :vendor AND ue.timestamp BETWEEN :startDate AND :endDate")
    List<UsageEvent> findByVendorAndDateRange(@Param("vendor") String vendor, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(ue) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate")
    Long countEventsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(ue) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate")
    Long countEventsByCustomerAndDateRange(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(ue.totalTokens), 0) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate")
    Long sumTotalTokensByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(ue.totalTokens), 0) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate")
    Long sumTotalTokensByCustomerAndDateRange(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(ue.totalCost), 0) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalCostByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(ue.totalCost), 0) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalCostByCustomerAndDateRange(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(ue.revenue), 0) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(ue.revenue), 0) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalRevenueByCustomerAndDateRange(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(ue.profit), 0) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalProfitByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(ue.profit), 0) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalProfitByCustomerAndDateRange(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.vendor, COUNT(ue) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.vendor")
    List<Object[]> countEventsByVendor(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.vendor, COUNT(ue) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.vendor")
    List<Object[]> countEventsByCustomerAndVendor(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.vendor, COALESCE(SUM(ue.totalTokens), 0) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.vendor")
    List<Object[]> sumTokensByVendor(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.vendor, COALESCE(SUM(ue.totalTokens), 0) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.vendor")
    List<Object[]> sumTokensByCustomerAndVendor(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.vendor, COALESCE(SUM(ue.totalCost), 0) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.vendor")
    List<Object[]> sumCostByVendor(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.vendor, COALESCE(SUM(ue.totalCost), 0) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.vendor")
    List<Object[]> sumCostByCustomerAndVendor(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.model, COUNT(ue) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.model")
    List<Object[]> countEventsByModel(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.model, COUNT(ue) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.model")
    List<Object[]> countEventsByCustomerAndModel(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.model, COALESCE(SUM(ue.totalTokens), 0) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.model")
    List<Object[]> sumTokensByModel(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.model, COALESCE(SUM(ue.totalTokens), 0) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.model")
    List<Object[]> sumTokensByCustomerAndModel(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.model, COALESCE(SUM(ue.totalCost), 0) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.model")
    List<Object[]> sumCostByModel(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.model, COALESCE(SUM(ue.totalCost), 0) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.model")
    List<Object[]> sumCostByCustomerAndModel(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.apiType, COUNT(ue) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.apiType")
    List<Object[]> countEventsByApiType(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.apiType, COUNT(ue) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.apiType")
    List<Object[]> countEventsByCustomerAndApiType(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.apiType, COALESCE(SUM(ue.totalTokens), 0) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.apiType")
    List<Object[]> sumTokensByApiType(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.apiType, COALESCE(SUM(ue.totalTokens), 0) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.apiType")
    List<Object[]> sumTokensByCustomerAndApiType(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.apiType, COALESCE(SUM(ue.totalCost), 0) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.apiType")
    List<Object[]> sumCostByApiType(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.apiType, COALESCE(SUM(ue.totalCost), 0) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.apiType")
    List<Object[]> sumCostByCustomerAndApiType(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.customer.customerId, ue.customer.organizationName, COUNT(ue), COALESCE(SUM(ue.totalTokens), 0), COALESCE(SUM(ue.totalCost), 0), COALESCE(SUM(ue.revenue), 0), COALESCE(SUM(ue.profit), 0) " +
           "FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY ue.customer.customerId, ue.customer.organizationName " +
           "ORDER BY COALESCE(SUM(ue.totalCost), 0) DESC")
    List<Object[]> getTopCustomers(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ue.user.userId, ue.user.firstName, ue.user.lastName, ue.user.email, ue.customer.customerId, COUNT(ue), COALESCE(SUM(ue.totalTokens), 0), COALESCE(SUM(ue.totalCost), 0), COALESCE(SUM(ue.revenue), 0), COALESCE(SUM(ue.profit), 0) " +
           "FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY ue.user.userId, ue.user.firstName, ue.user.lastName, ue.user.email, ue.customer.customerId " +
           "ORDER BY COALESCE(SUM(ue.totalCost), 0) DESC")
    List<Object[]> getTopUsers(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT DATE(ue.timestamp), COUNT(ue), COALESCE(SUM(ue.totalTokens), 0), COALESCE(SUM(ue.totalCost), 0), COALESCE(SUM(ue.revenue), 0), COALESCE(SUM(ue.profit), 0) " +
           "FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(ue.timestamp) " +
           "ORDER BY DATE(ue.timestamp)")
    List<Object[]> getTimeSeriesData(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT DATE(ue.timestamp), COUNT(ue), COALESCE(SUM(ue.totalTokens), 0), COALESCE(SUM(ue.totalCost), 0), COALESCE(SUM(ue.revenue), 0), COALESCE(SUM(ue.profit), 0) " +
           "FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(ue.timestamp) " +
           "ORDER BY DATE(ue.timestamp)")
    List<Object[]> getTimeSeriesDataByCustomer(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

       @Query("SELECT ue.region, COUNT(ue) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.region")
       List<Object[]> countEventsByRegion(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

       @Query("SELECT ue.region, COUNT(ue) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.region")
       List<Object[]> countEventsByCustomerAndRegion(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

       @Query("SELECT ue.endpoint, COUNT(ue) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.endpoint")
       List<Object[]> countEventsByEndpoint(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

       @Query("SELECT ue.endpoint, COUNT(ue) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.endpoint")
       List<Object[]> countEventsByCustomerAndEndpoint(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

       @Query("SELECT ue.user.role, COUNT(ue) FROM UsageEvent ue WHERE ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.user.role")
       List<Object[]> countEventsByUserRole(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

       @Query("SELECT ue.user.role, COUNT(ue) FROM UsageEvent ue WHERE ue.customer.customerId = :customerId AND ue.timestamp BETWEEN :startDate AND :endDate GROUP BY ue.user.role")
       List<Object[]> countEventsByCustomerAndUserRole(@Param("customerId") String customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    List<UsageEvent> findAll();
}
