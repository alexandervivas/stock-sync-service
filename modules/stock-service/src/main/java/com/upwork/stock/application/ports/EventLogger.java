package com.upwork.stock.application.ports;

import com.upwork.stock.events.OutOfStockEvent;

public interface EventLogger {
    void outOfStock(OutOfStockEvent event);
}
