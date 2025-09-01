package com.llmtracker.service;

import com.llmtracker.entity.UsageEvent;
import com.llmtracker.entity.VendorPricing;
import com.llmtracker.repository.VendorPricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class CostCalculationService {

    @Autowired
    private VendorPricingRepository vendorPricingRepository;

    public void calculateCosts(UsageEvent event) {
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal inputCost = BigDecimal.ZERO;
        BigDecimal outputCost = BigDecimal.ZERO;

        if (event.getInputTokens() != null && event.getInputTokens() > 0) {
            inputCost = calculateTokenCost(event.getVendor(), event.getModel(), event.getApiType(), 
                                         "input_tokens", event.getInputTokens());
        }

        if (event.getOutputTokens() != null && event.getOutputTokens() > 0) {
            outputCost = calculateTokenCost(event.getVendor(), event.getModel(), event.getApiType(), 
                                          "output_tokens", event.getOutputTokens());
        }

        if (event.getImageCount() != null && event.getImageCount() > 0) {
            BigDecimal imageCost = calculateImageCost(event.getVendor(), event.getModel(), event.getApiType(), 
                                                    event.getImageCount());
            totalCost = totalCost.add(imageCost);
        }

        if (event.getVideoCount() != null && event.getVideoCount() > 0) {
            BigDecimal videoCost = calculateVideoCost(event.getVendor(), event.getModel(), event.getApiType(), 
                                                    event.getVideoCount());
            totalCost = totalCost.add(videoCost);
        }

        if (event.getAudioMinutes() != null && event.getAudioMinutes().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal audioCost = calculateAudioCost(event.getVendor(), event.getModel(), event.getApiType(), 
                                                    event.getAudioMinutes());
            totalCost = totalCost.add(audioCost);
        }

        totalCost = totalCost.add(inputCost).add(outputCost);

        event.setInputCost(inputCost);
        event.setOutputCost(outputCost);
        event.setTotalCost(totalCost);

        calculateRevenueAndProfit(event);
    }

    private BigDecimal calculateTokenCost(String vendor, String model, String apiType, String metricType, Long tokens) {
        Optional<VendorPricing> pricing = vendorPricingRepository.findByVendorModelApiTypeAndMetric(
            vendor, model, apiType, metricType);

        if (pricing.isPresent()) {
            BigDecimal pricePerToken = pricing.get().getPricePerUnit();
            return pricePerToken.multiply(BigDecimal.valueOf(tokens));
        }

        return getDefaultTokenCost(vendor, model, metricType, tokens);
    }

    private BigDecimal calculateImageCost(String vendor, String model, String apiType, Integer imageCount) {
        Optional<VendorPricing> pricing = vendorPricingRepository.findByVendorModelApiTypeAndMetric(
            vendor, model, apiType, "image_count");

        if (pricing.isPresent()) {
            BigDecimal pricePerImage = pricing.get().getPricePerUnit();
            return pricePerImage.multiply(BigDecimal.valueOf(imageCount));
        }

        return getDefaultImageCost(vendor, model, imageCount);
    }

    private BigDecimal calculateVideoCost(String vendor, String model, String apiType, Integer videoCount) {
        Optional<VendorPricing> pricing = vendorPricingRepository.findByVendorModelApiTypeAndMetric(
            vendor, model, apiType, "video_count");

        if (pricing.isPresent()) {
            BigDecimal pricePerVideo = pricing.get().getPricePerUnit();
            return pricePerVideo.multiply(BigDecimal.valueOf(videoCount));
        }

        return getDefaultVideoCost(vendor, model, videoCount);
    }

    private BigDecimal calculateAudioCost(String vendor, String model, String apiType, BigDecimal audioMinutes) {
        Optional<VendorPricing> pricing = vendorPricingRepository.findByVendorModelApiTypeAndMetric(
            vendor, model, apiType, "audio_minutes");

        if (pricing.isPresent()) {
            BigDecimal pricePerMinute = pricing.get().getPricePerUnit();
            return pricePerMinute.multiply(audioMinutes);
        }

        return getDefaultAudioCost(vendor, model, audioMinutes);
    }

    private void calculateRevenueAndProfit(UsageEvent event) {
        BigDecimal totalCost = event.getTotalCost();
        Double markupPercentage = event.getCustomer().getMarkupPercentage();
        
        BigDecimal markupMultiplier = BigDecimal.ONE.add(BigDecimal.valueOf(markupPercentage / 100.0));
        BigDecimal revenue = totalCost.multiply(markupMultiplier).setScale(6, RoundingMode.HALF_UP);
        
        BigDecimal profit = revenue.subtract(totalCost).setScale(6, RoundingMode.HALF_UP);
        
        event.setRevenue(revenue);
        event.setProfit(profit);
    }

    private BigDecimal getDefaultTokenCost(String vendor, String model, String metricType, Long tokens) {
        BigDecimal pricePerToken = BigDecimal.ZERO;
        
        if ("OpenAI".equals(vendor)) {
            if ("gpt-4".equals(model)) {
                pricePerToken = "input_tokens".equals(metricType) ? 
                    new BigDecimal("0.00003") : new BigDecimal("0.00006");
            } else if ("gpt-3.5-turbo".equals(model)) {
                pricePerToken = "input_tokens".equals(metricType) ? 
                    new BigDecimal("0.0000015") : new BigDecimal("0.000002");
            }
        } else if ("Anthropic".equals(vendor)) {
            if ("claude-3-opus".equals(model)) {
                pricePerToken = "input_tokens".equals(metricType) ? 
                    new BigDecimal("0.000015") : new BigDecimal("0.000075");
            } else if ("claude-3-sonnet".equals(model)) {
                pricePerToken = "input_tokens".equals(metricType) ? 
                    new BigDecimal("0.000003") : new BigDecimal("0.000015");
            }
        } else if ("Google".equals(vendor)) {
            if ("gemini-pro".equals(model)) {
                pricePerToken = "input_tokens".equals(metricType) ? 
                    new BigDecimal("0.0000005") : new BigDecimal("0.0000015");
            }
        }
        
        return pricePerToken.multiply(BigDecimal.valueOf(tokens));
    }

    private BigDecimal getDefaultImageCost(String vendor, String model, Integer imageCount) {
        BigDecimal pricePerImage = BigDecimal.ZERO;
        
        if ("OpenAI".equals(vendor) && "dall-e-3".equals(model)) {
            pricePerImage = new BigDecimal("0.040");
        } else if ("OpenAI".equals(vendor) && "dall-e-2".equals(model)) {
            pricePerImage = new BigDecimal("0.020");
        }
        
        return pricePerImage.multiply(BigDecimal.valueOf(imageCount));
    }

    private BigDecimal getDefaultVideoCost(String vendor, String model, Integer videoCount) {
        BigDecimal pricePerVideo = BigDecimal.ZERO;
        
        if ("OpenAI".equals(vendor) && "sora".equals(model)) {
            pricePerVideo = new BigDecimal("0.050");
        }
        
        return pricePerVideo.multiply(BigDecimal.valueOf(videoCount));
    }

    private BigDecimal getDefaultAudioCost(String vendor, String model, BigDecimal audioMinutes) {
        BigDecimal pricePerMinute = BigDecimal.ZERO;
        
        if ("OpenAI".equals(vendor) && "whisper-1".equals(model)) {
            pricePerMinute = new BigDecimal("0.006");
        }
        
        return pricePerMinute.multiply(audioMinutes);
    }
}
