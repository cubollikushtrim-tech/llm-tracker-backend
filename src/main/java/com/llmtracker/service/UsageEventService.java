package com.llmtracker.service;

import com.llmtracker.dto.AnalyticsResponse;
import com.llmtracker.dto.UsageEventRequest;
import com.llmtracker.dto.UsageEventResponse;
import com.llmtracker.entity.Customer;
import com.llmtracker.entity.UsageEvent;
import com.llmtracker.entity.User;
import com.llmtracker.repository.CustomerRepository;
import com.llmtracker.repository.UsageEventRepository;
import com.llmtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsageEventService {

    @Autowired
    private UsageEventRepository usageEventRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CostCalculationService costCalculationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsageEventResponse processUsageEvent(UsageEventRequest request) {
        String eventId = generateEventId();

        Customer customer = getOrCreateCustomer(request);

        User user = getOrCreateUser(request, customer);

        UsageEvent event = new UsageEvent(eventId, request.getVendor(), request.getModel(), 
                                        request.getApiType(), customer, user, request.getTimestamp());

        event.setRegion(request.getRegion());
        event.setInputTokens(request.getInputTokens());
        event.setOutputTokens(request.getOutputTokens());
        event.setTotalTokens(request.getTotalTokens());
        event.setCachedTokens(request.getCachedTokens());
        event.setImageCount(request.getImageCount());
        event.setVideoCount(request.getVideoCount());
        event.setAudioMinutes(request.getAudioMinutes());
        event.setRequestCount(request.getRequestCount());

        event.setRequestId(request.getRequestId());
        event.setSessionId(request.getSessionId());
        event.setEndpoint(request.getEndpoint());
        event.setStatus(request.getStatus());
        event.setErrorMessage(request.getErrorMessage());
        event.setMetadata(request.getMetadata());

        costCalculationService.calculateCosts(event);

        UsageEvent savedEvent = usageEventRepository.save(event);

        return convertToResponse(savedEvent);
    }


    public Page<UsageEventResponse> getUsageEvents(String customerId, String userId, String vendor, 
                                                  String model, String apiType, LocalDateTime startDate, 
                                                  LocalDateTime endDate, Pageable pageable) {
        List<UsageEvent> allEvents = usageEventRepository.findAll();
        
        List<UsageEvent> filteredEvents = allEvents.stream()
            .filter(event -> customerId == null || customerId.isEmpty() || 
                           event.getCustomer().getCustomerId().equals(customerId))
            .filter(event -> userId == null || userId.isEmpty() || 
                           event.getUser().getUserId().equals(userId))
            .filter(event -> vendor == null || vendor.isEmpty() || 
                           event.getVendor().equals(vendor))
            .filter(event -> model == null || model.isEmpty() || 
                           event.getModel().equals(model))
            .filter(event -> apiType == null || apiType.isEmpty() || 
                           event.getApiType().equals(apiType))
            .filter(event -> startDate == null || 
                           event.getTimestamp().isAfter(startDate) || 
                           event.getTimestamp().isEqual(startDate))
            .filter(event -> endDate == null || 
                           event.getTimestamp().isBefore(endDate) || 
                           event.getTimestamp().isEqual(endDate))
            .collect(Collectors.toList());
        
        int total = filteredEvents.size();
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<UsageEvent> pageEvents = filteredEvents.subList(start, end);
        List<UsageEventResponse> pageResponses = pageEvents.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PageImpl<>(pageResponses, pageable, total);
    }

    public AnalyticsResponse getAnalytics(String period, LocalDate startDate, LocalDate endDate, 
                                        String customerId, String userId, String vendor) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        AnalyticsResponse response = new AnalyticsResponse(period, startDate, endDate);

        boolean filterByCustomer = customerId != null && !customerId.trim().isEmpty();

        if (filterByCustomer) {
            response.setTotalEvents(usageEventRepository.countEventsByCustomerAndDateRange(customerId, startDateTime, endDateTime));
            response.setTotalTokens(usageEventRepository.sumTotalTokensByCustomerAndDateRange(customerId, startDateTime, endDateTime));
            response.setTotalCost(usageEventRepository.sumTotalCostByCustomerAndDateRange(customerId, startDateTime, endDateTime));
            response.setTotalRevenue(usageEventRepository.sumTotalRevenueByCustomerAndDateRange(customerId, startDateTime, endDateTime));
            response.setTotalProfit(usageEventRepository.sumTotalProfitByCustomerAndDateRange(customerId, startDateTime, endDateTime));
        } else {
            response.setTotalEvents(usageEventRepository.countEventsByDateRange(startDateTime, endDateTime));
            response.setTotalTokens(usageEventRepository.sumTotalTokensByDateRange(startDateTime, endDateTime));
            response.setTotalCost(usageEventRepository.sumTotalCostByDateRange(startDateTime, endDateTime));
            response.setTotalRevenue(usageEventRepository.sumTotalRevenueByDateRange(startDateTime, endDateTime));
            response.setTotalProfit(usageEventRepository.sumTotalProfitByDateRange(startDateTime, endDateTime));
        }

        if (response.getTotalRevenue() != null && response.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profitMargin = response.getTotalProfit()
                .divide(response.getTotalRevenue(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            response.setProfitMargin(profitMargin);
        }

        if (filterByCustomer) {
            response.setUsageByVendor(convertToMap(usageEventRepository.countEventsByCustomerAndVendor(customerId, startDateTime, endDateTime)));
            response.setUsageByModel(convertToMap(usageEventRepository.countEventsByCustomerAndModel(customerId, startDateTime, endDateTime)));
            response.setUsageByApiType(convertToMap(usageEventRepository.countEventsByCustomerAndApiType(customerId, startDateTime, endDateTime)));

            response.setCostByVendor(convertToBigDecimalMap(usageEventRepository.sumCostByCustomerAndVendor(customerId, startDateTime, endDateTime)));
            response.setCostByModel(convertToBigDecimalMap(usageEventRepository.sumCostByCustomerAndModel(customerId, startDateTime, endDateTime)));
            response.setCostByApiType(convertToBigDecimalMap(usageEventRepository.sumCostByCustomerAndApiType(customerId, startDateTime, endDateTime)));

            response.setUsageByRegion(convertToMap(usageEventRepository.countEventsByCustomerAndRegion(customerId, startDateTime, endDateTime)));
            response.setUsageByEndpoint(convertToMap(usageEventRepository.countEventsByCustomerAndEndpoint(customerId, startDateTime, endDateTime)));
            response.setUsageByUserRole(convertToMap(usageEventRepository.countEventsByCustomerAndUserRole(customerId, startDateTime, endDateTime)));
        } else {
            response.setUsageByVendor(convertToMap(usageEventRepository.countEventsByVendor(startDateTime, endDateTime)));
            response.setUsageByModel(convertToMap(usageEventRepository.countEventsByModel(startDateTime, endDateTime)));
            response.setUsageByApiType(convertToMap(usageEventRepository.countEventsByApiType(startDateTime, endDateTime)));

            response.setCostByVendor(convertToBigDecimalMap(usageEventRepository.sumCostByVendor(startDateTime, endDateTime)));
            response.setCostByModel(convertToBigDecimalMap(usageEventRepository.sumCostByModel(startDateTime, endDateTime)));
            response.setCostByApiType(convertToBigDecimalMap(usageEventRepository.sumCostByApiType(startDateTime, endDateTime)));

            response.setUsageByRegion(convertToMap(usageEventRepository.countEventsByRegion(startDateTime, endDateTime)));
            response.setUsageByEndpoint(convertToMap(usageEventRepository.countEventsByEndpoint(startDateTime, endDateTime)));
            response.setUsageByUserRole(convertToMap(usageEventRepository.countEventsByUserRole(startDateTime, endDateTime)));
        }

        if (filterByCustomer) {
            response.setTimeSeriesData(convertToTimeSeriesData(usageEventRepository.getTimeSeriesDataByCustomer(customerId, startDateTime, endDateTime)));
        } else {
            response.setTimeSeriesData(convertToTimeSeriesData(usageEventRepository.getTimeSeriesData(startDateTime, endDateTime)));
        }

        if (filterByCustomer) {
            response.setTopCustomers(new ArrayList<>());
            response.setTopUsers(convertToUserMetrics(usageEventRepository.getTopUsers(startDateTime, endDateTime)));
        } else {
            response.setTopCustomers(convertToCustomerMetrics(usageEventRepository.getTopCustomers(startDateTime, endDateTime)));
            response.setTopUsers(convertToUserMetrics(usageEventRepository.getTopUsers(startDateTime, endDateTime)));
        }

        LocalDate prevStartDate = startDate.minusMonths(1);
        LocalDate prevEndDate = endDate.minusMonths(1);
        LocalDateTime prevStartDateTime = prevStartDate.atStartOfDay();
        LocalDateTime prevEndDateTime = prevEndDate.atTime(23, 59, 59);

        Long prevEvents;
        Long prevTokens;
        BigDecimal prevCost;
        BigDecimal prevRevenue;
        BigDecimal prevProfit;

        if (filterByCustomer) {
            prevEvents = usageEventRepository.countEventsByCustomerAndDateRange(customerId, prevStartDateTime, prevEndDateTime);
            prevTokens = usageEventRepository.sumTotalTokensByCustomerAndDateRange(customerId, prevStartDateTime, prevEndDateTime);
            prevCost = usageEventRepository.sumTotalCostByCustomerAndDateRange(customerId, prevStartDateTime, prevEndDateTime);
            prevRevenue = usageEventRepository.sumTotalRevenueByCustomerAndDateRange(customerId, prevStartDateTime, prevEndDateTime);
            prevProfit = usageEventRepository.sumTotalProfitByCustomerAndDateRange(customerId, prevStartDateTime, prevEndDateTime);
        } else {
            prevEvents = usageEventRepository.countEventsByDateRange(prevStartDateTime, prevEndDateTime);
            prevTokens = usageEventRepository.sumTotalTokensByDateRange(prevStartDateTime, prevEndDateTime);
            prevCost = usageEventRepository.sumTotalCostByDateRange(prevStartDateTime, prevEndDateTime);
            prevRevenue = usageEventRepository.sumTotalRevenueByDateRange(prevStartDateTime, prevEndDateTime);
            prevProfit = usageEventRepository.sumTotalProfitByDateRange(prevStartDateTime, prevEndDateTime);
        }

        AnalyticsResponse.GrowthMetrics growthMetrics = new AnalyticsResponse.GrowthMetrics();
        if (prevEvents != null && prevEvents > 0) {
            BigDecimal eventsGrowth = BigDecimal.valueOf(response.getTotalEvents() - prevEvents)
                .divide(BigDecimal.valueOf(prevEvents), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            growthMetrics.setEventsGrowth(eventsGrowth);
        }
        if (prevTokens != null && prevTokens > 0) {
            BigDecimal tokensGrowth = BigDecimal.valueOf(response.getTotalTokens() - prevTokens)
                .divide(BigDecimal.valueOf(prevTokens), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            growthMetrics.setTokensGrowth(tokensGrowth);
        }
        if (prevCost != null && prevCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal costGrowth = response.getTotalCost().subtract(prevCost)
                .divide(prevCost, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            growthMetrics.setCostGrowth(costGrowth);
        }
        if (prevRevenue != null && prevRevenue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal revenueGrowth = response.getTotalRevenue().subtract(prevRevenue)
                .divide(prevRevenue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            growthMetrics.setRevenueGrowth(revenueGrowth);
        }
        if (prevProfit != null && prevProfit.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profitGrowth = response.getTotalProfit().subtract(prevProfit)
                .divide(prevProfit, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            growthMetrics.setProfitGrowth(profitGrowth);
        }
        growthMetrics.setComparisonPeriod("previous_month");
        response.setGrowthMetrics(growthMetrics);

        AnalyticsResponse.Predictions predictions = new AnalyticsResponse.Predictions();
        predictions.setPredictedEvents(response.getTotalEvents());
        predictions.setPredictedTokens(response.getTotalTokens());
        predictions.setPredictedCost(response.getTotalCost());
        predictions.setPredictedRevenue(response.getTotalRevenue());
        predictions.setPredictedProfit(response.getTotalProfit());
        if (growthMetrics.getEventsGrowth() != null)
            predictions.setPredictedEvents((long)(response.getTotalEvents() * (1 + growthMetrics.getEventsGrowth().doubleValue() / 100)));
        if (growthMetrics.getTokensGrowth() != null)
            predictions.setPredictedTokens((long)(response.getTotalTokens() * (1 + growthMetrics.getTokensGrowth().doubleValue() / 100)));
        if (growthMetrics.getCostGrowth() != null)
            predictions.setPredictedCost(response.getTotalCost().multiply(BigDecimal.valueOf(1 + growthMetrics.getCostGrowth().doubleValue() / 100)));
        if (growthMetrics.getRevenueGrowth() != null)
            predictions.setPredictedRevenue(response.getTotalRevenue().multiply(BigDecimal.valueOf(1 + growthMetrics.getRevenueGrowth().doubleValue() / 100)));
        if (growthMetrics.getProfitGrowth() != null)
            predictions.setPredictedProfit(response.getTotalProfit().multiply(BigDecimal.valueOf(1 + growthMetrics.getProfitGrowth().doubleValue() / 100)));
        response.setPredictions(predictions);

        AnalyticsResponse.EfficiencyMetrics efficiency = new AnalyticsResponse.EfficiencyMetrics();
        if (response.getTotalEvents() != null && response.getTotalEvents() > 0) {
            efficiency.setCostPerEvent(response.getTotalCost().doubleValue() / response.getTotalEvents());
            efficiency.setRevenuePerEvent(response.getTotalRevenue().doubleValue() / response.getTotalEvents());
            efficiency.setProfitPerEvent(response.getTotalProfit().doubleValue() / response.getTotalEvents());
        }
        if (response.getTotalTokens() != null && response.getTotalTokens() > 0) {
            efficiency.setCostPerToken(response.getTotalCost().doubleValue() / response.getTotalTokens());
            efficiency.setRevenuePerToken(response.getTotalRevenue().doubleValue() / response.getTotalTokens());
            efficiency.setProfitPerToken(response.getTotalProfit().doubleValue() / response.getTotalTokens());
        }
        response.setEfficiencyMetrics(efficiency);

        List<AnalyticsResponse.TimeSeriesData> ts = response.getTimeSeriesData();
        Map<String, Double> weeklyPattern = new HashMap<>();
        Map<String, Double> monthlyPattern = new HashMap<>();
        Map<String, List<Long>> weekDayEvents = new HashMap<>();
        Map<String, List<Long>> monthEvents = new HashMap<>();
        java.time.format.TextStyle style = java.time.format.TextStyle.FULL;
        java.util.Locale locale = java.util.Locale.ENGLISH;
        for (AnalyticsResponse.TimeSeriesData d : ts) {
            String dayOfWeek = d.getDate().getDayOfWeek().getDisplayName(style, locale);
            String month = d.getDate().getMonth().getDisplayName(style, locale);
            weekDayEvents.computeIfAbsent(dayOfWeek, k -> new ArrayList<>()).add(d.getEvents());
            monthEvents.computeIfAbsent(month, k -> new ArrayList<>()).add(d.getEvents());
        }
        for (String day : weekDayEvents.keySet()) {
            List<Long> vals = weekDayEvents.get(day);
            weeklyPattern.put(day, vals.stream().mapToLong(l -> l).average().orElse(0));
        }
        for (String m : monthEvents.keySet()) {
            List<Long> vals = monthEvents.get(m);
            monthlyPattern.put(m, vals.stream().mapToLong(l -> l).average().orElse(0));
        }
        AnalyticsResponse.Seasonality seasonality = new AnalyticsResponse.Seasonality();
        seasonality.setWeeklyPattern(weeklyPattern);
        seasonality.setMonthlyPattern(monthlyPattern);
        response.setSeasonality(seasonality);

        List<AnalyticsResponse.Anomaly> anomalies = new ArrayList<>();
        double avgEvents = ts.stream().mapToLong(AnalyticsResponse.TimeSeriesData::getEvents).average().orElse(0);
        for (AnalyticsResponse.TimeSeriesData d : ts) {
            if (avgEvents > 0) {
                if (d.getEvents() > avgEvents * 2) {
                    AnalyticsResponse.Anomaly anomaly = new AnalyticsResponse.Anomaly();
                    anomaly.setDate(d.getDate());
                    anomaly.setType("spike");
                    anomaly.setMetric("events");
                    anomaly.setDescription("Events spike: " + d.getEvents() + " (avg: " + (long)avgEvents + ")");
                    anomalies.add(anomaly);
                } else if (d.getEvents() < avgEvents * 0.5) {
                    AnalyticsResponse.Anomaly anomaly = new AnalyticsResponse.Anomaly();
                    anomaly.setDate(d.getDate());
                    anomaly.setType("drop");
                    anomaly.setMetric("events");
                    anomaly.setDescription("Events drop: " + d.getEvents() + " (avg: " + (long)avgEvents + ")");
                    anomalies.add(anomaly);
                }
            }
        }
        response.setAnomalies(anomalies);

        return response;
    }

    private Customer getOrCreateCustomer(UsageEventRequest request) {
        return customerRepository.findByCustomerId(request.getCustomerId())
            .orElseGet(() -> {
                if (request.getCustomerDetails() != null) {
                    Customer customer = new Customer(request.getCustomerId(), 
                                                   request.getCustomerDetails().getOrganizationName(),
                                                   request.getCustomerDetails().getContactEmail());
                    customer.setContactPhone(request.getCustomerDetails().getContactPhone());
                    customer.setAddress(request.getCustomerDetails().getAddress());
                    return customerRepository.save(customer);
                } else {
                    throw new RuntimeException("Customer not found and no customer details provided");
                }
            });
    }

    private User getOrCreateUser(UsageEventRequest request, Customer customer) {
        return userRepository.findByUserId(request.getUserId())
            .orElseGet(() -> {
                if (request.getUserDetails() != null) {
                    User user = new User(request.getUserId(), 
                                       request.getUserDetails().getFirstName(),
                                       request.getUserDetails().getLastName(),
                                       request.getUserDetails().getEmail(),
                                       passwordEncoder.encode("defaultPassword123"),
                                       customer);
                    user.setDepartment(request.getUserDetails().getDepartment());
                    user.setRole(request.getUserDetails().getRole());
                    return userRepository.save(user);
                } else {
                    throw new RuntimeException("User not found and no user details provided");
                }
            });
    }

    private String generateEventId() {
        return "evt_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private UsageEventResponse convertToResponse(UsageEvent event) {
        UsageEventResponse response = new UsageEventResponse(
            event.getEventId(), event.getCustomer().getCustomerId(), event.getUser().getUserId(),
            event.getVendor(), event.getModel(), event.getApiType(), event.getTimestamp()
        );

        response.setRegion(event.getRegion());
        response.setInputTokens(event.getInputTokens());
        response.setOutputTokens(event.getOutputTokens());
        response.setTotalTokens(event.getTotalTokens());
        response.setCachedTokens(event.getCachedTokens());
        response.setImageCount(event.getImageCount());
        response.setVideoCount(event.getVideoCount());
        response.setAudioMinutes(event.getAudioMinutes());
        response.setRequestCount(event.getRequestCount());
        response.setInputCost(event.getInputCost());
        response.setOutputCost(event.getOutputCost());
        response.setTotalCost(event.getTotalCost());
        response.setRevenue(event.getRevenue());
        response.setProfit(event.getProfit());
        response.setCurrency(event.getCurrency());
        response.setRequestId(event.getRequestId());
        response.setSessionId(event.getSessionId());
        response.setEndpoint(event.getEndpoint());
        response.setStatus(event.getStatus());
        response.setErrorMessage(event.getErrorMessage());
        response.setMetadata(event.getMetadata());
        response.setCreatedAt(event.getCreatedAt());

        return response;
    }

    private Map<String, Long> convertToMap(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return new HashMap<>();
        }
        return results.stream()
            .filter(row -> row != null && row.length >= 2 && row[0] != null && row[1] != null)
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));
    }

    private Map<String, BigDecimal> convertToBigDecimalMap(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return new HashMap<>();
        }
        return results.stream()
            .filter(row -> row != null && row.length >= 2 && row[0] != null && row[1] != null)
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (BigDecimal) row[1]
            ));
    }

    private List<AnalyticsResponse.TimeSeriesData> convertToTimeSeriesData(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }
        return results.stream()
            .filter(row -> row != null && row.length >= 6)
            .map(row -> {
                LocalDate date;
                if (row[0] instanceof java.sql.Date) {
                    date = ((java.sql.Date) row[0]).toLocalDate();
                } else if (row[0] instanceof java.sql.Timestamp) {
                    date = ((java.sql.Timestamp) row[0]).toLocalDateTime().toLocalDate();
                } else {
                    date = (LocalDate) row[0];
                }
                
                AnalyticsResponse.TimeSeriesData data = new AnalyticsResponse.TimeSeriesData(date);
                data.setEvents(row[1] != null ? (Long) row[1] : 0L);
                data.setTokens(row[2] != null ? (Long) row[2] : 0L);
                data.setCost(row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO);
                data.setRevenue(row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO);
                data.setProfit(row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO);
                return data;
            })
            .collect(Collectors.toList());
    }

    private List<AnalyticsResponse.CustomerMetrics> convertToCustomerMetrics(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }
        return results.stream()
            .filter(row -> row != null && row.length >= 7)
            .map(row -> {
                AnalyticsResponse.CustomerMetrics metrics = new AnalyticsResponse.CustomerMetrics(
                    (String) row[0], (String) row[1]
                );
                metrics.setEvents(row[2] != null ? (Long) row[2] : 0L);
                metrics.setTokens(row[3] != null ? (Long) row[3] : 0L);
                metrics.setCost(row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO);
                metrics.setRevenue(row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO);
                metrics.setProfit(row[6] != null ? (BigDecimal) row[6] : BigDecimal.ZERO);
                
                if (metrics.getRevenue() != null && metrics.getRevenue().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal profitMargin = metrics.getProfit()
                        .divide(metrics.getRevenue(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                    metrics.setProfitMargin(profitMargin);
                }
                
                return metrics;
            })
            .collect(Collectors.toList());
    }

    private List<AnalyticsResponse.UserMetrics> convertToUserMetrics(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }
        return results.stream()
            .filter(row -> row != null && row.length >= 10)
            .map(row -> {
                AnalyticsResponse.UserMetrics metrics = new AnalyticsResponse.UserMetrics(
                    (String) row[0], (String) row[1], (String) row[2], (String) row[4]
                );
                metrics.setEvents(row[5] != null ? (Long) row[5] : 0L);
                metrics.setTokens(row[6] != null ? (Long) row[6] : 0L);
                metrics.setCost(row[7] != null ? (BigDecimal) row[7] : BigDecimal.ZERO);
                metrics.setRevenue(row[8] != null ? (BigDecimal) row[8] : BigDecimal.ZERO);
                metrics.setProfit(row[9] != null ? (BigDecimal) row[9] : BigDecimal.ZERO);
                return metrics;
            })
            .collect(Collectors.toList());
    }
}
