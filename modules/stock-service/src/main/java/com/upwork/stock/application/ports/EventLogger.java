package com.upwork.stock.application.ports;

import com.upwork.stock.application.events.OutOfStockEvent;

public interface EventLogger {
    void outOfStock(OutOfStockEvent event);
}
