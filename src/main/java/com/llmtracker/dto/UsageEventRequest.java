package com.llmtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UsageEventRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Vendor is required")
    private String vendor;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "API type is required")
    private String apiType;

    private String region;

    @Positive(message = "Input tokens must be positive")
    private Long inputTokens;

    @Positive(message = "Output tokens must be positive")
    private Long outputTokens;

    @Positive(message = "Total tokens must be positive")
    private Long totalTokens;

    @Positive(message = "Cached tokens must be positive")
    private Long cachedTokens;

    @Positive(message = "Image count must be positive")
    private Integer imageCount;

    @Positive(message = "Video count must be positive")
    private Integer videoCount;

    @Positive(message = "Audio minutes must be positive")
    private BigDecimal audioMinutes;

    @Positive(message = "Request count must be positive")
    private Integer requestCount = 1;

    private String requestId;
    private String sessionId;
    private String endpoint;
    private String status = "success";
    private String errorMessage;
    private String metadata;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    private CustomerDetails customerDetails;

    private UserDetails userDetails;

    public static class CustomerDetails {
        @NotBlank(message = "Organization name is required")
        private String organizationName;

        @NotBlank(message = "Contact email is required")
        private String contactEmail;

        private String contactPhone;
        private String address;

        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

        public String getContactEmail() { return contactEmail; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    public static class UserDetails {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Email is required")
        private String email;

        private String department;
        private String role;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

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

    public CustomerDetails getCustomerDetails() { return customerDetails; }
    public void setCustomerDetails(CustomerDetails customerDetails) { this.customerDetails = customerDetails; }

    public UserDetails getUserDetails() { return userDetails; }
    public void setUserDetails(UserDetails userDetails) { this.userDetails = userDetails; }
}
