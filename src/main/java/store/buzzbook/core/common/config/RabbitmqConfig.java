package store.buzzbook.core.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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

	public static final String REQUEST_EXCHANGE_NAME = "aa.coupon.exchange";
	public static final String REQUEST_QUEUE_NAME = "aa.coupon.queue";
	public static final String REQUEST_ROUTING_KEY = "aa.coupon.key";

	public static final String DLX_EXCHANGE_NAME = "aa.coupon.dlx.exchange";
	public static final String DLQ_QUEUE_NAME = "aa.coupon.dlx.queue";
	public static final String DLQ_ROUTING_KEY = "aa.coupon.dlx.key";

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
		return QueueBuilder.durable(REQUEST_QUEUE_NAME)
			.withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
			.withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
			.build();
	}

	@Bean
	Binding requestBinding(DirectExchange requestExchange, Queue requestQueue) {
		return BindingBuilder.bind(requestQueue).to(requestExchange).with(REQUEST_ROUTING_KEY);
	}

	@Bean
	DirectExchange dlxExchange() {
		return new DirectExchange(DLX_EXCHANGE_NAME);
	}

	@Bean
	Queue dlqQueue() {
		return new Queue(DLQ_QUEUE_NAME, true);
	}

	@Bean
	Binding dlqBinding(DirectExchange dlxExchange, Queue dlqQueue) {
		return BindingBuilder.bind(dlqQueue).to(dlxExchange).with(DLQ_ROUTING_KEY);
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
