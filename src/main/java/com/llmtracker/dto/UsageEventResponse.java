package com.llmtracker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UsageEventResponse {

    private String eventId;
    private String customerId;
    private String userId;
    private String vendor;
    private String model;
    private String apiType;
    private String region;
    
    private Long inputTokens;
    private Long outputTokens;
    private Long totalTokens;
    private Long cachedTokens;
    private Integer imageCount;
    private Integer videoCount;
    private BigDecimal audioMinutes;
    private Integer requestCount;
    
    private BigDecimal inputCost;
    private BigDecimal outputCost;
    private BigDecimal totalCost;
    private BigDecimal revenue;
    private BigDecimal profit;
    private String currency;
    
    private String requestId;
    private String sessionId;
    private String endpoint;
    private String status;
    private String errorMessage;
    private String metadata;
    
    private LocalDateTime timestamp;
    private LocalDateTime createdAt;

    public UsageEventResponse() {}

    public UsageEventResponse(String eventId, String customerId, String userId, String vendor, String model, 
                            String apiType, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.customerId = customerId;
        this.userId = userId;
        this.vendor = vendor;
        this.model = model;
        this.apiType = apiType;
        this.timestamp = timestamp;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getApiType() { return apiType; }
    public void setApiType(String apiType) { this.apiType = apiType; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public Long getInputTokens() { return inputTokens; }
    public void setInputTokens(Long inputTokens) { this.inputTokens = inputTokens; }

    public Long getOutputTokens() { return outputTokens; }
    public void setOutputTokens(Long outputTokens) { this.outputTokens = outputTokens; }

    public Long getTotalTokens() { return totalTokens; }
    public void setTotalTokens(Long totalTokens) { this.totalTokens = totalTokens; }

    public Long getCachedTokens() { return cachedTokens; }
    public void setCachedTokens(Long cachedTokens) { this.cachedTokens = cachedTokens; }

    public Integer getImageCount() { return imageCount; }
    public void setImageCount(Integer imageCount) { this.imageCount = imageCount; }

    public Integer getVideoCount() { return videoCount; }
    public void setVideoCount(Integer videoCount) { this.videoCount = videoCount; }

    public BigDecimal getAudioMinutes() { return audioMinutes; }
    public void setAudioMinutes(BigDecimal audioMinutes) { this.audioMinutes = audioMinutes; }

    public Integer getRequestCount() { return requestCount; }
    public void setRequestCount(Integer requestCount) { this.requestCount = requestCount; }

    public BigDecimal getInputCost() { return inputCost; }
    public void setInputCost(BigDecimal inputCost) { this.inputCost = inputCost; }

    public BigDecimal getOutputCost() { return outputCost; }
    public void setOutputCost(BigDecimal outputCost) { this.outputCost = outputCost; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

    public BigDecimal getProfit() { return profit; }
    public void setProfit(BigDecimal profit) { this.profit = profit; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
