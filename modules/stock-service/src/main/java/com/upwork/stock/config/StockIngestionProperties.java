package com.upwork.stock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ingestion")
public record StockIngestionProperties(VendorA vendorA, VendorB vendorB, Sync sync) {

    public record VendorA(String baseUrl) {

    }

    public record VendorB(String csvPath) {

    }

    public record Sync(String cron) {

    }
}
