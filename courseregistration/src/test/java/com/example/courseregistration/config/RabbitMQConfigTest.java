package com.example.courseregistration.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static com.example.courseregistration.config.RabbitMQConfig.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    void exchange_shouldUseCorrectName() {
        TopicExchange ex = config.exchange();
        assertEquals(EXCHANGE_NAME, ex.getName(), "Exchange name");
        assertTrue(ex instanceof TopicExchange, "Should be TopicExchange");
    }

    @Test
    void registrationQueue_shouldBeDurableAndHaveCorrectName() {
        Queue q = config.registrationQueue();
        assertEquals(REGISTRATION_QUEUE, q.getName(), "Queue name");
        assertTrue(q.isDurable(), "Queue should be durable");
    }

    @Test
    void registrationBinding_shouldBindQueueToExchangeWithWaitlistKey() {
        Queue q       = new Queue(REGISTRATION_QUEUE, true);
        TopicExchange ex = new TopicExchange(EXCHANGE_NAME);

        Binding b = config.registrationBinding(q, ex);

        assertEquals(EXCHANGE_NAME,    b.getExchange(),     "Binding exchange");
        assertEquals(REGISTRATION_QUEUE, b.getDestination(), "Binding destination");
        assertEquals(DestinationType.QUEUE, b.getDestinationType(), "Destination type");
        assertEquals("waitlist.#",     b.getRoutingKey(),   "Routing key for waitlist");
    }

    @Test
    void vacancyBinding_shouldBindQueueToExchangeWithVacancyKey() {
        Queue q       = new Queue(REGISTRATION_QUEUE, true);
        TopicExchange ex = new TopicExchange(EXCHANGE_NAME);

        Binding b = config.vacancyBinding(q, ex);

        assertEquals(EXCHANGE_NAME,    b.getExchange(),     "Binding exchange");
        assertEquals(REGISTRATION_QUEUE, b.getDestination(), "Binding destination");
        assertEquals(DestinationType.QUEUE, b.getDestinationType(), "Destination type");
        assertEquals("vacancy.#",      b.getRoutingKey(),   "Routing key for vacancy");
    }

    @Test
    void converter_shouldBeJackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter conv = config.converter();
        assertNotNull(conv, "Converter must not be null");
        // you can add more assertions here if you want to inspect the ObjectMapper
    }

    @Test
    void rabbitTemplate_shouldUseProvidedFactoryAndConverter() {
        ConnectionFactory cf = mock(ConnectionFactory.class);
        Jackson2JsonMessageConverter conv = new Jackson2JsonMessageConverter();

        RabbitTemplate tpl = config.rabbitTemplate(cf, conv);

        assertSame(cf,  tpl.getConnectionFactory(),  "Should use provided ConnectionFactory");
        assertSame(conv, tpl.getMessageConverter(),   "Should use provided MessageConverter");
    }
}
