package com.upwork.stock.eventlogger.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange stockEventsExchange(@Value("${events.exchange}") String name) {
        return new TopicExchange(name, true, false);
    }

    @Bean
    public Queue outOfStockQueue(@Value("${events.queue}") String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    public Binding outOfStockBinding(
            TopicExchange stockEventsExchange,
            Queue outOfStockQueue,
            @Value("${events.routingKey}") String routingKey) {
        return BindingBuilder.bind(outOfStockQueue)
                .to(stockEventsExchange)
                .with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
