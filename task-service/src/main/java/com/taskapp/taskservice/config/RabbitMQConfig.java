package com.taskapp.taskservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguracja RabbitMQ dla usługi operacyjnej.
 * Definiuje exchange, kolejkę oraz binding.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "task.exchange";
    public static final String QUEUE_NAME = "task.analytics.queue";
    public static final String ROUTING_KEY = "task.event.#";

    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue analyticsQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding analyticsBinding(Queue analyticsQueue, TopicExchange taskExchange) {
        return BindingBuilder
                .bind(analyticsQueue)
                .to(taskExchange)
                .with(ROUTING_KEY);
    }

    /** Konwerter JSON — komunikaty wysyłane jako JSON */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}