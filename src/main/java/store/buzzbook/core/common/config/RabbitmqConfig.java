package store.buzzbook.core.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitmqConfig {

	public static final String REQUEST_EXCHANGE_NAME = "aa.coupon.welcome.request.exchange";
	public static final String REQUEST_QUEUE_NAME = "aa.coupon.welcome.request.queue";
	public static final String REQUEST_ROUTING_KEY = "aa.coupon.welcome.request.key";

	public static final String RESPONSE_EXCHANGE_NAME = "aa.coupon.welcome.response.exchange";
	public static final String RESPONSE_QUEUE_NAME = "aa.coupon.welcome.response.queue";
	public static final String RESPONSE_ROUTING_KEY = "aa.coupon.welcome.response.key";

	@Value("${spring.rabbitmq.host}")
	private String host;

	@Value("${spring.rabbitmq.username}")
	private String username;

	@Value("${spring.rabbitmq.password}")
	private String password;

	@Value("${spring.rabbitmq.port}")
	private int port;

	@Bean
	DirectExchange requestExchange() {
		return new DirectExchange(REQUEST_EXCHANGE_NAME);
	}

	@Bean
	Queue requestQueue() {
		return new Queue(REQUEST_QUEUE_NAME, false);
	}

	@Bean
	Binding requestBinding(DirectExchange requestExchange, Queue requestQueue) {
		return BindingBuilder.bind(requestQueue).to(requestExchange).with(REQUEST_ROUTING_KEY);
	}

	@Bean
	DirectExchange responseExchange() {
		return new DirectExchange(RESPONSE_EXCHANGE_NAME);
	}

	@Bean
	Queue responseQueue() {
		return new Queue(RESPONSE_QUEUE_NAME, false);
	}

	@Bean
	Binding responseBinding(DirectExchange responseExchange, Queue responseQueue) {
		return BindingBuilder.bind(responseQueue).to(responseExchange).with(RESPONSE_ROUTING_KEY);
	}

	@Bean
	ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(host);
		connectionFactory.setPort(port);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		return connectionFactory;
	}

	@Bean
	MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter);
		return rabbitTemplate;
	}
}
