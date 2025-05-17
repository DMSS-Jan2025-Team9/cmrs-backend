package com.example.notificationservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class RabbitMQConfigTest {

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @MockBean
    private ConnectionFactory connectionFactory;

    @Test
    public void contextLoads() {
        assertNotNull(rabbitMQConfig);
    }

    @Test
    public void testExchangeCreation() {
        TopicExchange exchange = rabbitMQConfig.exchange();

        assertNotNull(exchange);
        assertEquals(RabbitMQConfig.EXCHANGE_NAME, exchange.getName());
    }

    @Test
    public void testRegistrationQueueCreation() {
        Queue queue = rabbitMQConfig.registrationQueue();

        assertNotNull(queue);
        assertEquals(RabbitMQConfig.REGISTRATION_QUEUE, queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    public void testRegistrationBindingCreation() {
        TopicExchange exchange = rabbitMQConfig.exchange();
        Queue queue = rabbitMQConfig.registrationQueue();

        Binding binding = rabbitMQConfig.registrationBinding(queue, exchange);

        assertNotNull(binding);
        assertEquals("waitlist.#", binding.getRoutingKey());
    }

    @Test
    public void testVacancyBindingCreation() {
        TopicExchange exchange = rabbitMQConfig.exchange();
        Queue queue = rabbitMQConfig.registrationQueue();

        Binding binding = rabbitMQConfig.vacancyBinding(queue, exchange);

        assertNotNull(binding);
        assertEquals("vacancy.#", binding.getRoutingKey());
    }

    @Test
    public void testMessageConverterCreation() {
        Jackson2JsonMessageConverter converter = rabbitMQConfig.converter();

        assertNotNull(converter);
    }

    @Test
    public void testRabbitTemplateCreation() {
        Jackson2JsonMessageConverter converter = rabbitMQConfig.converter();

        RabbitTemplate template = rabbitMQConfig.rabbitTemplate(connectionFactory, converter);

        assertNotNull(template);
    }
}