package com.upwork.stock.infrastructure.events;

import com.upwork.stock.events.OutOfStockEvent;
import com.upwork.stock.application.ports.EventLogger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "events.sink", havingValue = "rabbit")
public class RabbitEventLogger implements EventLogger {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public RabbitEventLogger(
            RabbitTemplate rabbitTemplate,
            @Value("${events.broker.exchange}") String exchange,
            @Value("${events.broker.routingKey}") String routingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void outOfStock(OutOfStockEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
