package com.upwork.vendor.b.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vendorb")
public record VendorBProperties(String csvPath, String schedule, Boolean generateSample) {

}