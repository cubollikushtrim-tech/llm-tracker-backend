package com.llmtracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AnalyticsResponse {

    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    
    private Long totalEvents;
    private Long totalTokens;
    private BigDecimal totalCost;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private BigDecimal profitMargin;
    
    private Map<String, Long> usageByVendor;
    private Map<String, Long> usageByModel;
    private Map<String, Long> usageByApiType;
    
    private Map<String, BigDecimal> costByVendor;
    private Map<String, BigDecimal> costByModel;
    private Map<String, BigDecimal> costByApiType;
    
    private List<TimeSeriesData> timeSeriesData;
    
    private List<CustomerMetrics> topCustomers;
    private List<UserMetrics> topUsers;
    
    private GrowthMetrics growthMetrics;

    private Predictions predictions;

    private List<Anomaly> anomalies;

    private Seasonality seasonality;

    private EfficiencyMetrics efficiencyMetrics;

    private Map<String, Long> usageByRegion;
    private Map<String, Long> usageByEndpoint;
    private Map<String, Long> usageByUserRole;

    public static class TimeSeriesData {
        private LocalDate date;
        private Long events;
        private Long tokens;
        private BigDecimal cost;
        private BigDecimal revenue;
        private BigDecimal profit;

        public TimeSeriesData() {}

        public TimeSeriesData(LocalDate date) {
            this.date = date;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public Long getEvents() { return events; }
        public void setEvents(Long events) { this.events = events; }

        public Long getTokens() { return tokens; }
        public void setTokens(Long tokens) { this.tokens = tokens; }

        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

        public BigDecimal getProfit() { return profit; }
        public void setProfit(BigDecimal profit) { this.profit = profit; }
    }

    public static class CustomerMetrics {
        private String customerId;
        private String organizationName;
        private Long events;
        private Long tokens;
        private BigDecimal cost;
        private BigDecimal revenue;
        private BigDecimal profit;
        private BigDecimal profitMargin;

        public CustomerMetrics() {}

        public CustomerMetrics(String customerId, String organizationName) {
            this.customerId = customerId;
            this.organizationName = organizationName;
        }

        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }

        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

        public Long getEvents() { return events; }
        public void setEvents(Long events) { this.events = events; }

        public Long getTokens() { return tokens; }
        public void setTokens(Long tokens) { this.tokens = tokens; }

        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

        public BigDecimal getProfit() { return profit; }
        public void setProfit(BigDecimal profit) { this.profit = profit; }

        public BigDecimal getProfitMargin() { return profitMargin; }
        public void setProfitMargin(BigDecimal profitMargin) { this.profitMargin = profitMargin; }
    }

    public static class UserMetrics {
        private String userId;
        private String fullName;
        private String email;
        private String customerId;
        private Long events;
        private Long tokens;
        private BigDecimal cost;
        private BigDecimal revenue;
        private BigDecimal profit;

        public UserMetrics() {}

        public UserMetrics(String userId, String fullName, String email, String customerId) {
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.customerId = customerId;
        }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }

        public Long getEvents() { return events; }
        public void setEvents(Long events) { this.events = events; }

        public Long getTokens() { return tokens; }
        public void setTokens(Long tokens) { this.tokens = tokens; }

        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

        public BigDecimal getProfit() { return profit; }
        public void setProfit(BigDecimal profit) { this.profit = profit; }
    }

    public static class GrowthMetrics {
        private BigDecimal eventsGrowth;
        private BigDecimal tokensGrowth;
        private BigDecimal costGrowth;
        private BigDecimal revenueGrowth;
        private BigDecimal profitGrowth;
        private String comparisonPeriod;

        public GrowthMetrics() {}

        public BigDecimal getEventsGrowth() { return eventsGrowth; }
        public void setEventsGrowth(BigDecimal eventsGrowth) { this.eventsGrowth = eventsGrowth; }

        public BigDecimal getTokensGrowth() { return tokensGrowth; }
        public void setTokensGrowth(BigDecimal tokensGrowth) { this.tokensGrowth = tokensGrowth; }

        public BigDecimal getCostGrowth() { return costGrowth; }
        public void setCostGrowth(BigDecimal costGrowth) { this.costGrowth = costGrowth; }

        public BigDecimal getRevenueGrowth() { return revenueGrowth; }
        public void setRevenueGrowth(BigDecimal revenueGrowth) { this.revenueGrowth = revenueGrowth; }

        public BigDecimal getProfitGrowth() { return profitGrowth; }
        public void setProfitGrowth(BigDecimal profitGrowth) { this.profitGrowth = profitGrowth; }

        public String getComparisonPeriod() { return comparisonPeriod; }
        public void setComparisonPeriod(String comparisonPeriod) { this.comparisonPeriod = comparisonPeriod; }
    }

    public static class Predictions {
        private Long predictedEvents;
        private Long predictedTokens;
        private BigDecimal predictedCost;
        private BigDecimal predictedRevenue;
        private BigDecimal predictedProfit;

        public Predictions() {}

        public Long getPredictedEvents() { return predictedEvents; }
        public void setPredictedEvents(Long predictedEvents) { this.predictedEvents = predictedEvents; }
        public Long getPredictedTokens() { return predictedTokens; }
        public void setPredictedTokens(Long predictedTokens) { this.predictedTokens = predictedTokens; }
        public BigDecimal getPredictedCost() { return predictedCost; }
        public void setPredictedCost(BigDecimal predictedCost) { this.predictedCost = predictedCost; }
        public BigDecimal getPredictedRevenue() { return predictedRevenue; }
        public void setPredictedRevenue(BigDecimal predictedRevenue) { this.predictedRevenue = predictedRevenue; }
        public BigDecimal getPredictedProfit() { return predictedProfit; }
        public void setPredictedProfit(BigDecimal predictedProfit) { this.predictedProfit = predictedProfit; }
    }

    public static class Anomaly {
        private LocalDate date;
        private String type;
        private String metric;
        private String description;

        public Anomaly() {}

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getMetric() { return metric; }
        public void setMetric(String metric) { this.metric = metric; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class Seasonality {
        private Map<String, Double> weeklyPattern;
        private Map<String, Double> monthlyPattern;

        public Seasonality() {}

        public Map<String, Double> getWeeklyPattern() { return weeklyPattern; }
        public void setWeeklyPattern(Map<String, Double> weeklyPattern) { this.weeklyPattern = weeklyPattern; }
        public Map<String, Double> getMonthlyPattern() { return monthlyPattern; }
        public void setMonthlyPattern(Map<String, Double> monthlyPattern) { this.monthlyPattern = monthlyPattern; }
    }

    public static class EfficiencyMetrics {
        private Double costPerEvent;
        private Double revenuePerEvent;
        private Double profitPerEvent;
        private Double costPerToken;
        private Double revenuePerToken;
        private Double profitPerToken;

        public EfficiencyMetrics() {}

        public Double getCostPerEvent() { return costPerEvent; }
        public void setCostPerEvent(Double costPerEvent) { this.costPerEvent = costPerEvent; }
        public Double getRevenuePerEvent() { return revenuePerEvent; }
        public void setRevenuePerEvent(Double revenuePerEvent) { this.revenuePerEvent = revenuePerEvent; }
        public Double getProfitPerEvent() { return profitPerEvent; }
        public void setProfitPerEvent(Double profitPerEvent) { this.profitPerEvent = profitPerEvent; }
        public Double getCostPerToken() { return costPerToken; }
        public void setCostPerToken(Double costPerToken) { this.costPerToken = costPerToken; }
        public Double getRevenuePerToken() { return revenuePerToken; }
        public void setRevenuePerToken(Double revenuePerToken) { this.revenuePerToken = revenuePerToken; }
        public Double getProfitPerToken() { return profitPerToken; }
        public void setProfitPerToken(Double profitPerToken) { this.profitPerToken = profitPerToken; }
    }

    public AnalyticsResponse() {}

    public AnalyticsResponse(String period, LocalDate startDate, LocalDate endDate) {
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Long getTotalEvents() { return totalEvents; }
    public void setTotalEvents(Long totalEvents) { this.totalEvents = totalEvents; }

    public Long getTotalTokens() { return totalTokens; }
    public void setTotalTokens(Long totalTokens) { this.totalTokens = totalTokens; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

    public BigDecimal getProfitMargin() { return profitMargin; }
    public void setProfitMargin(BigDecimal profitMargin) { this.profitMargin = profitMargin; }

    public Map<String, Long> getUsageByVendor() { return usageByVendor; }
    public void setUsageByVendor(Map<String, Long> usageByVendor) { this.usageByVendor = usageByVendor; }

    public Map<String, Long> getUsageByModel() { return usageByModel; }
    public void setUsageByModel(Map<String, Long> usageByModel) { this.usageByModel = usageByModel; }

    public Map<String, Long> getUsageByApiType() { return usageByApiType; }
    public void setUsageByApiType(Map<String, Long> usageByApiType) { this.usageByApiType = usageByApiType; }

    public Map<String, BigDecimal> getCostByVendor() { return costByVendor; }
    public void setCostByVendor(Map<String, BigDecimal> costByVendor) { this.costByVendor = costByVendor; }

    public Map<String, BigDecimal> getCostByModel() { return costByModel; }
    public void setCostByModel(Map<String, BigDecimal> costByModel) { this.costByModel = costByModel; }

    public Map<String, BigDecimal> getCostByApiType() { return costByApiType; }
    public void setCostByApiType(Map<String, BigDecimal> costByApiType) { this.costByApiType = costByApiType; }

    public List<TimeSeriesData> getTimeSeriesData() { return timeSeriesData; }
    public void setTimeSeriesData(List<TimeSeriesData> timeSeriesData) { this.timeSeriesData = timeSeriesData; }

    public List<CustomerMetrics> getTopCustomers() { return topCustomers; }
    public void setTopCustomers(List<CustomerMetrics> topCustomers) { this.topCustomers = topCustomers; }

    public List<UserMetrics> getTopUsers() { return topUsers; }
    public void setTopUsers(List<UserMetrics> topUsers) { this.topUsers = topUsers; }

    public GrowthMetrics getGrowthMetrics() { return growthMetrics; }
    public void setGrowthMetrics(GrowthMetrics growthMetrics) { this.growthMetrics = growthMetrics; }

    public Predictions getPredictions() { return predictions; }
    public void setPredictions(Predictions predictions) { this.predictions = predictions; }

    public List<Anomaly> getAnomalies() { return anomalies; }
    public void setAnomalies(List<Anomaly> anomalies) { this.anomalies = anomalies; }

    public Seasonality getSeasonality() { return seasonality; }
    public void setSeasonality(Seasonality seasonality) { this.seasonality = seasonality; }

    public EfficiencyMetrics getEfficiencyMetrics() { return efficiencyMetrics; }
    public void setEfficiencyMetrics(EfficiencyMetrics efficiencyMetrics) { this.efficiencyMetrics = efficiencyMetrics; }

    public Map<String, Long> getUsageByRegion() { return usageByRegion; }
    public void setUsageByRegion(Map<String, Long> usageByRegion) { this.usageByRegion = usageByRegion; }

    public Map<String, Long> getUsageByEndpoint() { return usageByEndpoint; }
    public void setUsageByEndpoint(Map<String, Long> usageByEndpoint) { this.usageByEndpoint = usageByEndpoint; }

    public Map<String, Long> getUsageByUserRole() { return usageByUserRole; }
    public void setUsageByUserRole(Map<String, Long> usageByUserRole) { this.usageByUserRole = usageByUserRole; }
}
