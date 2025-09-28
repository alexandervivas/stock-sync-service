package com.upwork.stock.eventlogger.events;

import java.time.Instant;

public record OutOfStockEvent(String sku, String vendor, int previousQty, int newQty, Instant occurredAt) {

}
