package com.upwork.stock.eventlogger.consumers;

import com.upwork.stock.eventlogger.events.OutOfStockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OutOfStockListener {

    private static final Logger log = LoggerFactory.getLogger(OutOfStockListener.class);

    private final CopyOnWriteArrayList<OutOfStockEvent> inMemory = new CopyOnWriteArrayList<>();

    @RabbitListener(queues = "#{outOfStockQueue.name}")
    public void handle(OutOfStockEvent event) {
        log.info("EVENT CONSUMED type=OUT_OF_STOCK sku={} vendor={} prev={} now={} ts={}",
                event.sku(), event.vendor(), event.previousQty(), event.newQty(), event.occurredAt());
        inMemory.add(event);
    }

    public List<OutOfStockEvent> events() {
        return List.copyOf(inMemory);
    }
}
