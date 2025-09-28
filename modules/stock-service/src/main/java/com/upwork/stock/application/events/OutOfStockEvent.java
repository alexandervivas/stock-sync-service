package com.upwork.stock.application.events;

public record OutOfStockEvent(String sku, String vendor, int previousQty, int newQty, java.time.Instant occurredAt) {

}
