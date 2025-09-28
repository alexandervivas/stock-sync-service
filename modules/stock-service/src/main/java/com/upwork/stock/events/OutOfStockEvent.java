package com.upwork.stock.events;

import java.time.Instant;

public record OutOfStockEvent(String sku, String vendor, int previousQty, int newQty, Instant occurredAt) {

}
