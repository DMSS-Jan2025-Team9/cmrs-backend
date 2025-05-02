package com.example.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "course-exchange";
    public static final String REGISTRATION_QUEUE = "registration-queue";
    public static final String WAITLIST_ROUTING_KEY = "waitlist.notification";
    public static final String VACANCY_ROUTING_KEY = "vacancy.notification";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue registrationQueue() {
        return new Queue(REGISTRATION_QUEUE, true);
    }

    @Bean
    public Binding registrationBinding(Queue registrationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(registrationQueue).to(exchange).with("waitlist.#");
    }

    @Bean
    public Binding vacancyBinding(Queue registrationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(registrationQueue).to(exchange).with("vacancy.#");
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}