package com.upwork.stock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ingestion")
public record StockIngestionProperties(VendorA vendorA, VendorB vendorB) {

    public record VendorA(String baseUrl) {

    }

    public record VendorB(String baseUrl) {

    }
}
