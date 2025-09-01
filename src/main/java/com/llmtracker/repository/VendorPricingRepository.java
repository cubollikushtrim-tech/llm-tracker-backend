package com.llmtracker.repository;

import com.llmtracker.entity.VendorPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorPricingRepository extends JpaRepository<VendorPricing, Long> {

    List<VendorPricing> findByVendor(String vendor);
    
    List<VendorPricing> findByModel(String model);
    
    List<VendorPricing> findByApiType(String apiType);
    
    List<VendorPricing> findByActiveTrue();
    
    @Query("SELECT vp FROM VendorPricing vp WHERE vp.vendor = :vendor AND vp.model = :model AND vp.apiType = :apiType AND vp.metricType = :metricType AND vp.active = true")
    Optional<VendorPricing> findByVendorModelApiTypeAndMetric(@Param("vendor") String vendor,
                                                             @Param("model") String model,
                                                             @Param("apiType") String apiType,
                                                             @Param("metricType") String metricType);
    
    @Query("SELECT DISTINCT vp.vendor FROM VendorPricing vp WHERE vp.active = true")
    List<String> findDistinctVendors();
    
    @Query("SELECT DISTINCT vp.model FROM VendorPricing vp WHERE vp.vendor = :vendor AND vp.active = true")
    List<String> findDistinctModelsByVendor(@Param("vendor") String vendor);
    
    @Query("SELECT DISTINCT vp.apiType FROM VendorPricing vp WHERE vp.active = true")
    List<String> findDistinctApiTypes();
    
    @Query("SELECT DISTINCT vp.metricType FROM VendorPricing vp WHERE vp.vendor = :vendor AND vp.model = :model AND vp.apiType = :apiType AND vp.active = true")
    List<String> findDistinctMetricTypes(@Param("vendor") String vendor,
                                        @Param("model") String model,
                                        @Param("apiType") String apiType);
}
