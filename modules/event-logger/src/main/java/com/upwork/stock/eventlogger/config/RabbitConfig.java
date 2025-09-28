package com.upwork.stock.eventlogger.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean(name = "rabbitListenerContainerFactory")
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);
        return factory;
    }
}
