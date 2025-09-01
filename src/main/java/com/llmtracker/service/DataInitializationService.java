package com.llmtracker.service;

import com.llmtracker.entity.Customer;
import com.llmtracker.entity.User;
import com.llmtracker.entity.UsageEvent;
import com.llmtracker.entity.VendorPricing;
import com.llmtracker.repository.CustomerRepository;
import com.llmtracker.repository.UserRepository;
import com.llmtracker.repository.UsageEventRepository;
import com.llmtracker.repository.VendorPricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DataInitializationService implements CommandLineRunner {
    public void addRandomEvents(int count, boolean todayOnly) {
        var customers = customerRepository.findAll();
        var users = userRepository.findAll();
        users.removeIf(u -> u.getRole() != null && u.getRole().equalsIgnoreCase("SUPERADMIN"));
        String[] vendors = {"OpenAI", "Anthropic", "Google"};
        String[] models = {"gpt-4", "gpt-3.5-turbo", "claude-3-sonnet", "claude-3-opus", "gemini-pro", "dall-e-3", "whisper-1"};
        String[] apiTypes = {"text", "image", "audio", "video"};
        String[] regions = {"us-east-1", "eu-west-1", "ap-southeast-1"};
        java.util.Random rand = new java.util.Random();
        LocalDateTime startDate = todayOnly ? LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0) : LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime endDate = todayOnly ? LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999) : LocalDateTime.of(2025, 8, 31, 23, 59);
        int eventIdCounter = 0;
        int totalEvents = 0;
        int maxEvents = count;
        for (LocalDateTime date = startDate; date.isBefore(endDate) && totalEvents < maxEvents; date = date.plusDays(todayOnly ? 0 : (rand.nextBoolean() ? 1 : 2))) {
            for (Customer customer : customers) {
                var customerUsers = users.stream().filter(u -> u.getCustomer() != null && u.getCustomer().getId().equals(customer.getId())).toList();
                if (customerUsers.isEmpty()) continue;
                int numEvents = todayOnly ? 1 : rand.nextInt(2);
                for (int i = 0; i < numEvents && totalEvents < maxEvents; i++) {
                    User user = customerUsers.get(rand.nextInt(customerUsers.size()));
                    String vendor = vendors[rand.nextInt(vendors.length)];
                    String model = models[rand.nextInt(models.length)];
                    String apiType = apiTypes[rand.nextInt(apiTypes.length)];
                    String region = regions[rand.nextInt(regions.length)];
                    LocalDateTime timestamp = todayOnly ? LocalDateTime.now().withHour(rand.nextInt(24)).withMinute(rand.nextInt(60)) : date.plusHours(rand.nextInt(24)).plusMinutes(rand.nextInt(60));
                    String eventId = "evt_rand_" + eventIdCounter++ + "_" + rand.nextInt(100000);
                    UsageEvent event = new UsageEvent(eventId, vendor, model, apiType, customer, user, timestamp);
                    event.setRegion(region);
                    event.setRequestCount(1);
                    event.setCurrency("USD");
                    event.setRequestId("req_" + eventId);
                    event.setSessionId("sess_" + eventId);
                    if (apiType.equals("text")) {
                        event.setEndpoint("/v1/chat/completions");
                    } else if (apiType.equals("image")) {
                        event.setEndpoint("/v1/images/generations");
                    } else if (apiType.equals("audio")) {
                        event.setEndpoint("/v1/audio/transcriptions");
                    } else if (apiType.equals("video")) {
                        event.setEndpoint("/v1/video/generations");
                    } else {
                        event.setEndpoint("/v1/other");
                    }
                    event.setStatus("success");
                    event.setMetadata("{} ");
                    if (apiType.equals("text")) {
                        long inputTokens = 500 + rand.nextInt(5000);
                        long outputTokens = 250 + rand.nextInt(2500);
                        long totalTokens = inputTokens + outputTokens;
                        event.setInputTokens(inputTokens);
                        event.setOutputTokens(outputTokens);
                        event.setTotalTokens(totalTokens);
                        event.setCachedTokens((long)rand.nextInt(100));
                        event.setInputCost(BigDecimal.valueOf(inputTokens * 0.00001));
                        event.setOutputCost(BigDecimal.valueOf(outputTokens * 0.00002));
                        event.setTotalCost(event.getInputCost().add(event.getOutputCost()));
                        event.setRevenue(event.getTotalCost().multiply(BigDecimal.valueOf(1.3 + rand.nextDouble() * 0.7)));
                        event.setProfit(event.getRevenue().subtract(event.getTotalCost()));
                    } else if (apiType.equals("image")) {
                        int imageCount = 1 + rand.nextInt(5);
                        event.setImageCount(imageCount);
                        event.setTotalCost(BigDecimal.valueOf(imageCount * 0.04 + rand.nextDouble() * 0.06));
                        event.setRevenue(event.getTotalCost().multiply(BigDecimal.valueOf(1.3 + rand.nextDouble() * 0.7)));
                        event.setProfit(event.getRevenue().subtract(event.getTotalCost()));
                    } else if (apiType.equals("audio")) {
                        BigDecimal audioMinutes = BigDecimal.valueOf(1 + rand.nextInt(30));
                        event.setAudioMinutes(audioMinutes);
                        event.setTotalCost(audioMinutes.multiply(BigDecimal.valueOf(0.006 + rand.nextDouble() * 0.004)));
                        event.setRevenue(event.getTotalCost().multiply(BigDecimal.valueOf(1.3 + rand.nextDouble() * 0.7)));
                        event.setProfit(event.getRevenue().subtract(event.getTotalCost()));
                    } else if (apiType.equals("video")) {
                        int videoCount = 1 + rand.nextInt(3);
                        event.setVideoCount(videoCount);
                        event.setTotalCost(BigDecimal.valueOf(videoCount * 0.15 + rand.nextDouble() * 0.1));
                        event.setRevenue(event.getTotalCost().multiply(BigDecimal.valueOf(1.3 + rand.nextDouble() * 0.7)));
                        event.setProfit(event.getRevenue().subtract(event.getTotalCost()));
                    }
                    usageEventRepository.save(event);
                    totalEvents++;
                }
            }
            if (todayOnly) break;
        }
    }

    @Autowired
    private VendorPricingRepository vendorPricingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsageEventRepository usageEventRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeVendorPricing();
        initializeSampleCustomers();
        var customers = customerRepository.findAll();
        var users = userRepository.findAll();
        users.removeIf(u -> u.getRole() != null && u.getRole().equalsIgnoreCase("SUPERADMIN"));
        String[] vendors = {"OpenAI", "Anthropic", "Google"};
        String[] models = {"gpt-4", "gpt-3.5-turbo", "claude-3-sonnet", "claude-3-opus", "gemini-pro", "dall-e-3", "whisper-1"};
        String[] apiTypes = {"text", "image", "audio", "video"};
        String[] regions = {"us-east-1", "eu-west-1", "ap-southeast-1"};
        java.util.Random rand = new java.util.Random();
        LocalDateTime startDate = LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 31, 23, 59);
        int eventIdCounter = 0;
        for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusDays(rand.nextBoolean() ? 1 : 2)) {
            for (Customer customer : customers) {
                var customerUsers = users.stream().filter(u -> u.getCustomer() != null && u.getCustomer().getId().equals(customer.getId())).toList();
                if (customerUsers.isEmpty()) continue;
                int numEvents = rand.nextInt(2);
                for (int i = 0; i < numEvents; i++) {
                    User user = customerUsers.get(rand.nextInt(customerUsers.size()));
                    String vendor = vendors[rand.nextInt(vendors.length)];
                    String model = models[rand.nextInt(models.length)];
                    String apiType = apiTypes[rand.nextInt(apiTypes.length)];
                    String region = regions[rand.nextInt(regions.length)];
                    LocalDateTime timestamp = date.plusHours(rand.nextInt(24)).plusMinutes(rand.nextInt(60));
                    String eventId = "evt_startup_" + eventIdCounter++ + "_" + rand.nextInt(100000);
                    UsageEvent event = new UsageEvent(eventId, vendor, model, apiType, customer, user, timestamp);
                    event.setRegion(region);
                    event.setRequestCount(1);
                    event.setCurrency("USD");
                    event.setRequestId("req_" + eventId);
                    event.setSessionId("sess_" + eventId);
                    if (apiType.equals("text")) {
                        event.setEndpoint("/v1/chat/completions");
                    } else if (apiType.equals("image")) {
                        event.setEndpoint("/v1/images/generations");
                    } else if (apiType.equals("audio")) {
                        event.setEndpoint("/v1/audio/transcriptions");
                    } else if (apiType.equals("video")) {
                        event.setEndpoint("/v1/video/generations");
                    } else {
                        event.setEndpoint("/v1/other");
                    }
                    event.setStatus("success");
                    event.setMetadata("{} ");
                    if (apiType.equals("text")) {
                        long inputTokens = 500 + rand.nextInt(5000);
                        long outputTokens = 250 + rand.nextInt(2500);
                        long totalTokens = inputTokens + outputTokens;
                        event.setInputTokens(inputTokens);
                        event.setOutputTokens(outputTokens);
                        event.setTotalTokens(totalTokens);
                        event.setCachedTokens((long)rand.nextInt(100));
                        event.setInputCost(BigDecimal.valueOf(inputTokens * 0.00001));
                        event.setOutputCost(BigDecimal.valueOf(outputTokens * 0.00002));
                        event.setTotalCost(event.getInputCost().add(event.getOutputCost()));
                        event.setRevenue(event.getTotalCost().multiply(BigDecimal.valueOf(1.3 + rand.nextDouble() * 0.7)));
                        event.setProfit(event.getRevenue().subtract(event.getTotalCost()));
                    } else if (apiType.equals("image")) {
                        int imageCount = 1 + rand.nextInt(5);
                        event.setImageCount(imageCount);
                        event.setTotalCost(BigDecimal.valueOf(imageCount * 0.04 + rand.nextDouble() * 0.06));
                        event.setRevenue(event.getTotalCost().multiply(BigDecimal.valueOf(1.3 + rand.nextDouble() * 0.7)));
                        event.setProfit(event.getRevenue().subtract(event.getTotalCost()));
                    } else if (apiType.equals("audio")) {
                        BigDecimal audioMinutes = BigDecimal.valueOf(1 + rand.nextInt(30));
                        event.setAudioMinutes(audioMinutes);
                        event.setTotalCost(audioMinutes.multiply(BigDecimal.valueOf(0.006 + rand.nextDouble() * 0.004)));
                        event.setRevenue(event.getTotalCost().multiply(BigDecimal.valueOf(1.3 + rand.nextDouble() * 0.7)));
                        event.setProfit(event.getRevenue().subtract(event.getTotalCost()));
                    } else if (apiType.equals("video")) {
                        int videoCount = 1 + rand.nextInt(3);
                        event.setVideoCount(videoCount);
                        event.setTotalCost(BigDecimal.valueOf(videoCount * 0.15 + rand.nextDouble() * 0.1));
                        event.setRevenue(event.getTotalCost().multiply(BigDecimal.valueOf(1.3 + rand.nextDouble() * 0.7)));
                        event.setProfit(event.getRevenue().subtract(event.getTotalCost()));
                    }
                    usageEventRepository.save(event);
                }
            }
        }
    }

    private void initializeVendorPricing() {
        
        if (vendorPricingRepository.count() > 0) {
            return;
        }

        
        vendorPricingRepository.save(new VendorPricing("OpenAI", "gpt-4", "text", "input_tokens", new BigDecimal("0.00003")));
        vendorPricingRepository.save(new VendorPricing("OpenAI", "gpt-4", "text", "output_tokens", new BigDecimal("0.00006")));
        vendorPricingRepository.save(new VendorPricing("OpenAI", "gpt-4-turbo", "text", "input_tokens", new BigDecimal("0.00001")));
        vendorPricingRepository.save(new VendorPricing("OpenAI", "gpt-4-turbo", "text", "output_tokens", new BigDecimal("0.00003")));

        
        vendorPricingRepository.save(new VendorPricing("OpenAI", "gpt-3.5-turbo", "text", "input_tokens", new BigDecimal("0.0000015")));
        vendorPricingRepository.save(new VendorPricing("OpenAI", "gpt-3.5-turbo", "text", "output_tokens", new BigDecimal("0.000002")));

        
        vendorPricingRepository.save(new VendorPricing("OpenAI", "dall-e-3", "image", "image_count", new BigDecimal("0.040")));
        vendorPricingRepository.save(new VendorPricing("OpenAI", "dall-e-2", "image", "image_count", new BigDecimal("0.020")));

        
        vendorPricingRepository.save(new VendorPricing("OpenAI", "whisper-1", "audio", "audio_minutes", new BigDecimal("0.006")));

        
        vendorPricingRepository.save(new VendorPricing("Anthropic", "claude-3-opus", "text", "input_tokens", new BigDecimal("0.000015")));
        vendorPricingRepository.save(new VendorPricing("Anthropic", "claude-3-opus", "text", "output_tokens", new BigDecimal("0.000075")));
        vendorPricingRepository.save(new VendorPricing("Anthropic", "claude-3-sonnet", "text", "input_tokens", new BigDecimal("0.000003")));
        vendorPricingRepository.save(new VendorPricing("Anthropic", "claude-3-sonnet", "text", "output_tokens", new BigDecimal("0.000015")));
        vendorPricingRepository.save(new VendorPricing("Anthropic", "claude-3-haiku", "text", "input_tokens", new BigDecimal("0.00000025")));
        vendorPricingRepository.save(new VendorPricing("Anthropic", "claude-3-haiku", "text", "output_tokens", new BigDecimal("0.00000125")));

        
        vendorPricingRepository.save(new VendorPricing("Google", "gemini-pro", "text", "input_tokens", new BigDecimal("0.0000005")));
        vendorPricingRepository.save(new VendorPricing("Google", "gemini-pro", "text", "output_tokens", new BigDecimal("0.0000015")));
        vendorPricingRepository.save(new VendorPricing("Google", "gemini-pro-vision", "image", "image_count", new BigDecimal("0.0025")));

        
        vendorPricingRepository.save(new VendorPricing("Cohere", "command", "text", "input_tokens", new BigDecimal("0.0000015")));
        vendorPricingRepository.save(new VendorPricing("Cohere", "command", "text", "output_tokens", new BigDecimal("0.000002")));

        
        vendorPricingRepository.save(new VendorPricing("Mistral", "mistral-large", "text", "input_tokens", new BigDecimal("0.000007")));
        vendorPricingRepository.save(new VendorPricing("Mistral", "mistral-large", "text", "output_tokens", new BigDecimal("0.000024")));
        vendorPricingRepository.save(new VendorPricing("Mistral", "mistral-medium", "text", "input_tokens", new BigDecimal("0.0000027")));
        vendorPricingRepository.save(new VendorPricing("Mistral", "mistral-medium", "text", "output_tokens", new BigDecimal("0.0000081")));

        System.out.println("Vendor pricing data initialized successfully!");
    }

    private void initializeSampleCustomers() {
        
        if (customerRepository.count() > 0) {
            return;
        }

        
        Customer customer1 = new Customer("cust_001", "TechCorp Solutions", "contact@techcorp.com");
        customer1.setContactPhone("+1-555-0123");
        customer1.setAddress("123 Tech Street, Silicon Valley, CA 94025");
        customer1.setMarkupPercentage(25.0);
        customerRepository.save(customer1);

        Customer customer2 = new Customer("cust_002", "DataFlow Analytics", "info@dataflow.com");
        customer2.setContactPhone("+1-555-0456");
        customer2.setAddress("456 Data Avenue, Austin, TX 73301");
        customer2.setMarkupPercentage(35.0);
        customerRepository.save(customer2);

        Customer customer3 = new Customer("cust_003", "AI Innovations Inc", "hello@aiinnovations.com");
        customer3.setContactPhone("+1-555-0789");
        customer3.setAddress("789 AI Boulevard, Seattle, WA 98101");
        customer3.setMarkupPercentage(40.0);
        customerRepository.save(customer3);

        
        User user1 = new User("user_001", "John", "Smith", "john.smith@techcorp.com", 
            passwordEncoder.encode("password123"), customer1);
        user1.setDepartment("Engineering");
        user1.setRole("ADMIN");
        userRepository.save(user1);

        User user2 = new User("user_002", "Sarah", "Johnson", "sarah.johnson@techcorp.com", 
            passwordEncoder.encode("password123"), customer1);
        user2.setDepartment("Product");
        user2.setRole("USER");
        userRepository.save(user2);

        User user3 = new User("user_003", "Mike", "Davis", "mike.davis@dataflow.com", 
            passwordEncoder.encode("password123"), customer2);
        user3.setDepartment("Data Science");
        user3.setRole("ADMIN");
        userRepository.save(user3);

        User user4 = new User("user_004", "Lisa", "Wang", "lisa.wang@aiinnovations.com", 
            passwordEncoder.encode("password123"), customer3);
        user4.setDepartment("Research");
        user4.setRole("USER");
        userRepository.save(user4);

        
        User superAdmin = new User("user_005", "System", "Administrator", "admin@llmtracker.com", 
            passwordEncoder.encode("password123"), null);
        superAdmin.setDepartment("System Administration");
        superAdmin.setRole("SUPERADMIN");
        userRepository.save(superAdmin);

        System.out.println("Sample customers and users initialized successfully!");
    }

    private void initializeSampleEvents() {
        
        if (usageEventRepository.count() > 0) {
            return;
        }

        try {
            
            Customer customer1 = customerRepository.findByCustomerId("cust_001").get();
            Customer customer2 = customerRepository.findByCustomerId("cust_002").get();
            Customer customer3 = customerRepository.findByCustomerId("cust_003").get();
            
            User user1 = userRepository.findByUserId("user_001").get();
            User user2 = userRepository.findByUserId("user_002").get();
            User user3 = userRepository.findByUserId("user_003").get();
            User user4 = userRepository.findByUserId("user_004").get();

            
            createSampleEvent("evt_dec_001", customer1, user1, "OpenAI", "gpt-4", "text", Long.valueOf(1200), Long.valueOf(600), Long.valueOf(1800), new BigDecimal("0.036"), new BigDecimal("0.036"), new BigDecimal("0.072"), new BigDecimal("0.0936"), new BigDecimal("0.0216"), "2024-12-15 09:30:00");
            createSampleEvent("evt_dec_002", customer2, user3, "Anthropic", "claude-3-sonnet", "text", Long.valueOf(800), Long.valueOf(400), Long.valueOf(1200), new BigDecimal("0.0024"), new BigDecimal("0.006"), new BigDecimal("0.0084"), new BigDecimal("0.01134"), new BigDecimal("0.00294"), "2024-12-15 14:15:00");
            createSampleEvent("evt_dec_003", customer3, user4, "Google", "gemini-pro", "text", Long.valueOf(600), Long.valueOf(300), Long.valueOf(900), new BigDecimal("0.0003"), new BigDecimal("0.00045"), new BigDecimal("0.00075"), new BigDecimal("0.00105"), new BigDecimal("0.0003"), "2024-12-16 11:20:00");
            
            
            createSampleEvent("evt_jan_001", customer1, user1, "OpenAI", "gpt-4", "text", Long.valueOf(2000), Long.valueOf(1000), Long.valueOf(3000), new BigDecimal("0.06"), new BigDecimal("0.06"), new BigDecimal("0.12"), new BigDecimal("0.156"), new BigDecimal("0.036"), "2025-01-05 08:45:00");
            createSampleEvent("evt_jan_002", customer1, user2, "OpenAI", "gpt-4-turbo", "text", Long.valueOf(1800), Long.valueOf(900), Long.valueOf(2700), new BigDecimal("0.018"), new BigDecimal("0.027"), new BigDecimal("0.045"), new BigDecimal("0.0585"), new BigDecimal("0.0135"), "2025-01-05 10:30:00");
            createSampleEvent("evt_jan_003", customer2, user3, "Anthropic", "claude-3-opus", "text", Long.valueOf(2500), Long.valueOf(1200), Long.valueOf(3700), new BigDecimal("0.0375"), new BigDecimal("0.09"), new BigDecimal("0.1275"), new BigDecimal("0.172125"), new BigDecimal("0.044625"), "2025-01-06 13:20:00");
            createSampleEvent("evt_jan_004", customer3, user4, "Google", "gemini-pro", "text", Long.valueOf(1500), Long.valueOf(750), Long.valueOf(2250), new BigDecimal("0.00075"), new BigDecimal("0.001125"), new BigDecimal("0.001875"), new BigDecimal("0.002625"), new BigDecimal("0.00075"), "2025-01-07 16:10:00");
            
            
            createSampleEvent("evt_feb_001", customer1, user1, "OpenAI", "gpt-3.5-turbo", "text", Long.valueOf(1000), Long.valueOf(500), Long.valueOf(1500), new BigDecimal("0.0015"), new BigDecimal("0.001"), new BigDecimal("0.0025"), new BigDecimal("0.00325"), new BigDecimal("0.00075"), "2025-02-14 12:00:00");
            createSampleEvent("evt_feb_002", customer2, user3, "Anthropic", "claude-3-sonnet", "text", Long.valueOf(1200), Long.valueOf(600), Long.valueOf(1800), new BigDecimal("0.0036"), new BigDecimal("0.009"), new BigDecimal("0.0126"), new BigDecimal("0.01701"), new BigDecimal("0.00441"), "2025-02-15 09:30:00");
            
            
            createSampleEvent("evt_mar_001", customer1, user1, "OpenAI", "gpt-4", "text", Long.valueOf(2200), Long.valueOf(1100), Long.valueOf(3300), new BigDecimal("0.066"), new BigDecimal("0.066"), new BigDecimal("0.132"), new BigDecimal("0.1716"), new BigDecimal("0.0396"), "2025-03-10 08:15:00");
            createSampleEvent("evt_mar_002", customer1, user2, "OpenAI", "gpt-4", "text", Long.valueOf(1800), Long.valueOf(900), Long.valueOf(2700), new BigDecimal("0.054"), new BigDecimal("0.054"), new BigDecimal("0.108"), new BigDecimal("0.1404"), new BigDecimal("0.0324"), "2025-03-10 14:30:00");
            createSampleEvent("evt_mar_003", customer2, user3, "Anthropic", "claude-3-opus", "text", Long.valueOf(3000), Long.valueOf(1500), Long.valueOf(4500), new BigDecimal("0.045"), new BigDecimal("0.1125"), new BigDecimal("0.1575"), new BigDecimal("0.212625"), new BigDecimal("0.055125"), "2025-03-11 11:45:00");
            createSampleEvent("evt_mar_004", customer3, user4, "Google", "gemini-pro", "text", Long.valueOf(2000), Long.valueOf(1000), Long.valueOf(3000), new BigDecimal("0.001"), new BigDecimal("0.0015"), new BigDecimal("0.0025"), new BigDecimal("0.0035"), new BigDecimal("0.001"), "2025-03-12 16:20:00");
            
            
            createSampleEvent("evt_apr_001", customer1, user1, "OpenAI", "gpt-4", "text", Long.valueOf(3000), Long.valueOf(1500), Long.valueOf(4500), new BigDecimal("0.09"), new BigDecimal("0.09"), new BigDecimal("0.18"), new BigDecimal("0.234"), new BigDecimal("0.054"), "2025-04-15 07:30:00");
            createSampleEvent("evt_apr_002", customer1, user2, "OpenAI", "gpt-4", "text", Long.valueOf(2500), Long.valueOf(1250), Long.valueOf(3750), new BigDecimal("0.075"), new BigDecimal("0.075"), new BigDecimal("0.15"), new BigDecimal("0.195"), new BigDecimal("0.045"), "2025-04-15 10:15:00");
            createSampleEvent("evt_apr_003", customer2, user3, "Anthropic", "claude-3-sonnet", "text", Long.valueOf(1800), Long.valueOf(900), Long.valueOf(2700), new BigDecimal("0.0054"), new BigDecimal("0.0135"), new BigDecimal("0.0189"), new BigDecimal("0.025515"), new BigDecimal("0.006615"), "2025-04-16 13:45:00");
            createSampleEvent("evt_apr_004", customer3, user4, "Google", "gemini-pro", "text", Long.valueOf(1200), Long.valueOf(600), Long.valueOf(1800), new BigDecimal("0.0006"), new BigDecimal("0.0009"), new BigDecimal("0.0015"), new BigDecimal("0.0021"), new BigDecimal("0.0006"), "2025-04-17 15:30:00");
            
            
            createSampleEvent("evt_may_001", customer1, user1, "OpenAI", "gpt-4", "text", Long.valueOf(1600), Long.valueOf(800), Long.valueOf(2400), new BigDecimal("0.048"), new BigDecimal("0.048"), new BigDecimal("0.096"), new BigDecimal("0.1248"), new BigDecimal("0.0288"), "2025-05-20 09:00:00");
            createSampleEvent("evt_may_002", customer2, user3, "Anthropic", "claude-3-opus", "text", Long.valueOf(2200), Long.valueOf(1100), Long.valueOf(3300), new BigDecimal("0.033"), new BigDecimal("0.0825"), new BigDecimal("0.1155"), new BigDecimal("0.155925"), new BigDecimal("0.040425"), "2025-05-21 14:20:00");
            
            
            createSampleEvent("evt_jun_001", customer1, user1, "OpenAI", "gpt-3.5-turbo", "text", Long.valueOf(800), Long.valueOf(400), Long.valueOf(1200), new BigDecimal("0.0012"), new BigDecimal("0.0008"), new BigDecimal("0.002"), new BigDecimal("0.0026"), new BigDecimal("0.0006"), "2025-06-15 11:30:00");
            createSampleEvent("evt_jun_002", customer2, user3, "Anthropic", "claude-3-sonnet", "text", Long.valueOf(1000), Long.valueOf(500), Long.valueOf(1500), new BigDecimal("0.003"), new BigDecimal("0.0075"), new BigDecimal("0.0105"), new BigDecimal("0.014175"), new BigDecimal("0.003675"), "2025-06-16 16:45:00");
            
            
            createSampleEvent("evt_jul_001", customer1, user1, "OpenAI", "gpt-4", "text", Long.valueOf(1400), Long.valueOf(700), Long.valueOf(2100), new BigDecimal("0.042"), new BigDecimal("0.042"), new BigDecimal("0.084"), new BigDecimal("0.1092"), new BigDecimal("0.0252"), "2025-07-10 08:30:00");
            createSampleEvent("evt_jul_002", customer1, user2, "OpenAI", "gpt-4", "text", Long.valueOf(1200), Long.valueOf(600), Long.valueOf(1800), new BigDecimal("0.036"), new BigDecimal("0.036"), new BigDecimal("0.072"), new BigDecimal("0.0936"), new BigDecimal("0.0216"), "2025-07-10 12:15:00");
            createSampleEvent("evt_jul_003", customer2, user3, "Anthropic", "claude-3-opus", "text", Long.valueOf(1800), Long.valueOf(900), Long.valueOf(2700), new BigDecimal("0.027"), new BigDecimal("0.0675"), new BigDecimal("0.0945"), new BigDecimal("0.127575"), new BigDecimal("0.033075"), "2025-07-11 14:20:00");
            createSampleEvent("evt_jul_004", customer3, user4, "Google", "gemini-pro", "text", Long.valueOf(900), Long.valueOf(450), Long.valueOf(1350), new BigDecimal("0.00045"), new BigDecimal("0.000675"), new BigDecimal("0.001125"), new BigDecimal("0.001575"), new BigDecimal("0.00045"), "2025-07-12 17:30:00");
            
            
            createSampleEvent("evt_aug_001", customer1, user1, "OpenAI", "gpt-4", "text", Long.valueOf(2800), Long.valueOf(1400), Long.valueOf(4200), new BigDecimal("0.084"), new BigDecimal("0.084"), new BigDecimal("0.168"), new BigDecimal("0.2184"), new BigDecimal("0.0504"), "2025-08-01 07:45:00");
            createSampleEvent("evt_aug_002", customer1, user2, "OpenAI", "gpt-4", "text", Long.valueOf(2400), Long.valueOf(1200), Long.valueOf(3600), new BigDecimal("0.072"), new BigDecimal("0.072"), new BigDecimal("0.144"), new BigDecimal("0.1872"), new BigDecimal("0.0432"), "2025-08-01 10:30:00");
            createSampleEvent("evt_aug_003", customer2, user3, "Anthropic", "claude-3-opus", "text", Long.valueOf(3200), Long.valueOf(1600), Long.valueOf(4800), new BigDecimal("0.048"), new BigDecimal("0.12"), new BigDecimal("0.168"), new BigDecimal("0.2268"), new BigDecimal("0.0588"), "2025-08-02 13:15:00");
            createSampleEvent("evt_aug_004", customer3, user4, "Google", "gemini-pro", "text", Long.valueOf(1800), Long.valueOf(900), Long.valueOf(2700), new BigDecimal("0.0009"), new BigDecimal("0.00135"), new BigDecimal("0.00225"), new BigDecimal("0.00315"), new BigDecimal("0.0009"), "2025-08-02 15:45:00");
            
            
            createImageEvent("evt_img_001", customer1, user1, "OpenAI", "dall-e-3", "image", Integer.valueOf(3), new BigDecimal("0.12"), new BigDecimal("0.156"), new BigDecimal("0.036"), "2024-12-20 14:30:00");
            createImageEvent("evt_img_002", customer2, user3, "OpenAI", "dall-e-2", "image", Integer.valueOf(2), new BigDecimal("0.04"), new BigDecimal("0.054"), new BigDecimal("0.014"), "2025-01-15 16:20:00");
            createImageEvent("evt_img_003", customer3, user4, "OpenAI", "dall-e-3", "image", Integer.valueOf(1), new BigDecimal("0.04"), new BigDecimal("0.052"), new BigDecimal("0.012"), "2025-02-14 11:45:00");
            createImageEvent("evt_img_004", customer1, user2, "OpenAI", "dall-e-3", "image", Integer.valueOf(4), new BigDecimal("0.16"), new BigDecimal("0.208"), new BigDecimal("0.048"), "2025-03-20 13:30:00");
            createImageEvent("evt_img_005", customer2, user3, "OpenAI", "dall-e-2", "image", Integer.valueOf(2), new BigDecimal("0.04"), new BigDecimal("0.054"), new BigDecimal("0.014"), "2025-04-15 10:15:00");
            createImageEvent("evt_img_006", customer3, user4, "OpenAI", "dall-e-3", "image", Integer.valueOf(3), new BigDecimal("0.12"), new BigDecimal("0.156"), new BigDecimal("0.036"), "2025-05-25 14:45:00");
            createImageEvent("evt_img_007", customer1, user1, "OpenAI", "dall-e-2", "image", Integer.valueOf(1), new BigDecimal("0.02"), new BigDecimal("0.027"), new BigDecimal("0.007"), "2025-06-20 16:30:00");
            createImageEvent("evt_img_008", customer2, user3, "OpenAI", "dall-e-3", "image", Integer.valueOf(2), new BigDecimal("0.08"), new BigDecimal("0.104"), new BigDecimal("0.024"), "2025-07-15 12:20:00");
            createImageEvent("evt_img_009", customer3, user4, "OpenAI", "dall-e-3", "image", Integer.valueOf(5), new BigDecimal("0.20"), new BigDecimal("0.26"), new BigDecimal("0.06"), "2025-08-05 09:15:00");
            
            
            createAudioEvent("evt_audio_001", customer1, user1, "OpenAI", "whisper-1", "audio", new BigDecimal("12.5"), new BigDecimal("0.075"), new BigDecimal("0.0975"), new BigDecimal("0.0225"), "2024-12-25 15:30:00");
            createAudioEvent("evt_audio_002", customer2, user3, "OpenAI", "whisper-1", "audio", new BigDecimal("8.0"), new BigDecimal("0.048"), new BigDecimal("0.0624"), new BigDecimal("0.0144"), "2025-01-20 11:45:00");
            createAudioEvent("evt_audio_003", customer3, user4, "OpenAI", "whisper-1", "audio", new BigDecimal("15.0"), new BigDecimal("0.09"), new BigDecimal("0.117"), new BigDecimal("0.027"), "2025-02-28 14:20:00");
            createAudioEvent("evt_audio_004", customer1, user2, "OpenAI", "whisper-1", "audio", new BigDecimal("6.5"), new BigDecimal("0.039"), new BigDecimal("0.0507"), new BigDecimal("0.0117"), "2025-03-25 16:15:00");
            createAudioEvent("evt_audio_005", customer2, user3, "OpenAI", "whisper-1", "audio", new BigDecimal("10.0"), new BigDecimal("0.06"), new BigDecimal("0.078"), new BigDecimal("0.018"), "2025-04-30 13:45:00");
            createAudioEvent("evt_audio_006", customer3, user4, "OpenAI", "whisper-1", "audio", new BigDecimal("7.5"), new BigDecimal("0.045"), new BigDecimal("0.0585"), new BigDecimal("0.0135"), "2025-05-30 10:30:00");
            createAudioEvent("evt_audio_007", customer1, user1, "OpenAI", "whisper-1", "audio", new BigDecimal("5.0"), new BigDecimal("0.03"), new BigDecimal("0.039"), new BigDecimal("0.009"), "2025-06-25 17:20:00");
            createAudioEvent("evt_audio_008", customer2, user3, "OpenAI", "whisper-1", "audio", new BigDecimal("9.0"), new BigDecimal("0.054"), new BigDecimal("0.0702"), new BigDecimal("0.0162"), "2025-07-20 12:10:00");
            createAudioEvent("evt_audio_009", customer3, user4, "OpenAI", "whisper-1", "audio", new BigDecimal("11.0"), new BigDecimal("0.066"), new BigDecimal("0.0858"), new BigDecimal("0.0198"), "2025-08-10 15:45:00");
            
            
            createSampleEvent("evt_mixed_001", customer1, user1, "Mistral", "mistral-large", "text", Long.valueOf(1600), Long.valueOf(800), Long.valueOf(2400), new BigDecimal("0.0112"), new BigDecimal("0.0192"), new BigDecimal("0.0304"), new BigDecimal("0.03952"), new BigDecimal("0.00912"), "2025-01-25 14:30:00");
            createSampleEvent("evt_mixed_002", customer2, user3, "Cohere", "command", "text", Long.valueOf(1200), Long.valueOf(600), Long.valueOf(1800), new BigDecimal("0.0018"), new BigDecimal("0.0012"), new BigDecimal("0.003"), new BigDecimal("0.0039"), new BigDecimal("0.0009"), "2025-03-15 11:20:00");
            createSampleEvent("evt_mixed_003", customer3, user4, "Mistral", "mistral-medium", "text", Long.valueOf(1000), Long.valueOf(500), Long.valueOf(1500), new BigDecimal("0.0027"), new BigDecimal("0.00405"), new BigDecimal("0.00675"), new BigDecimal("0.008775"), new BigDecimal("0.002025"), "2025-05-10 16:45:00");
            
            System.out.println("Comprehensive sample events initialized successfully! Total events: " + usageEventRepository.count());
            
        } catch (Exception e) {
            System.err.println("Error initializing sample events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createSampleEvent(String eventId, Customer customer, User user, String vendor, String model, 
                                 String apiType, Long inputTokens, Long outputTokens, Long totalTokens,
                                 BigDecimal inputCost, BigDecimal outputCost, BigDecimal totalCost, 
                                 BigDecimal revenue, BigDecimal profit, String timestamp) {
        UsageEvent event = new UsageEvent(eventId, vendor, model, apiType, customer, user, 
                                        LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        event.setRegion("us-east-1");
        event.setInputTokens(inputTokens);
        event.setOutputTokens(outputTokens);
        event.setTotalTokens(totalTokens);
        event.setCachedTokens(0L);
        event.setRequestCount(1);
        event.setInputCost(inputCost);
        event.setOutputCost(outputCost);
        event.setTotalCost(totalCost);
        event.setRevenue(revenue);
        event.setProfit(profit);
        event.setCurrency("USD");
        event.setRequestId("req_" + eventId);
        event.setSessionId("sess_" + eventId);
        event.setEndpoint("/v1/chat/completions");
        event.setStatus("success");
        event.setMetadata("{\"temperature\": 0.7, \"max_tokens\": 1000}");
        
        usageEventRepository.save(event);
    }

    private void createImageEvent(String eventId, Customer customer, User user, String vendor, String model,
                                String apiType, Integer imageCount, BigDecimal totalCost, BigDecimal revenue, 
                                BigDecimal profit, String timestamp) {
        UsageEvent event = new UsageEvent(eventId, vendor, model, apiType, customer, user,
                                        LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        event.setRegion("us-east-1");
        event.setImageCount(imageCount);
        event.setRequestCount(1);
        event.setTotalCost(totalCost);
        event.setRevenue(revenue);
        event.setProfit(profit);
        event.setCurrency("USD");
        event.setRequestId("req_" + eventId);
        event.setSessionId("sess_" + eventId);
        event.setEndpoint("/v1/images/generations");
        event.setStatus("success");
        event.setMetadata("{\"size\": \"1024x1024\", \"quality\": \"hd\"}");
        
        usageEventRepository.save(event);
    }

    private void createAudioEvent(String eventId, Customer customer, User user, String vendor, String model,
                                String apiType, BigDecimal audioMinutes, BigDecimal totalCost, BigDecimal revenue,
                                BigDecimal profit, String timestamp) {
        UsageEvent event = new UsageEvent(eventId, vendor, model, apiType, customer, user,
                                        LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        event.setRegion("us-east-1");
        event.setAudioMinutes(audioMinutes);
        event.setRequestCount(1);
        event.setTotalCost(totalCost);
        event.setRevenue(revenue);
        event.setProfit(profit);
        event.setCurrency("USD");
        event.setRequestId("req_" + eventId);
        event.setSessionId("sess_" + eventId);
        event.setEndpoint("/v1/audio/transcriptions");
        event.setStatus("success");
        event.setMetadata("{\"response_format\": \"json\", \"language\": \"en\"}");
        
        usageEventRepository.save(event);
    }
}
