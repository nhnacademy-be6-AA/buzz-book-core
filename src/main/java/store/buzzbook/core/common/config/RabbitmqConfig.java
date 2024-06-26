package store.buzzbook.core.common.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
	//
	// @Value("${spring.rabbitmq.host}")
	// private String host;
	//
	// @Value("${spring.rabbitmq.username}")
	// private String username;
	//
	// @Value("${spring.rabbitmq.password}")
	// private String password;
	//
	// @Value("${spring.rabbitmq.port}")
	// private int port;
	//
	// @Bean
	// DirectExchange directExchange() {
	// 	return new DirectExchange("aa-coupon.exchange");
	// }
	//
	// @Bean
	// Queue queue() {
	// 	return new Queue("aa-coupon.queue", false);
	// }
	//
	// @Bean
	// Binding binding(DirectExchange directExchange, Queue queue) {
	// 	return BindingBuilder.bind(queue).to(directExchange).with("aa-coupon.key");
	// }
	//
	// @Bean
	// ConnectionFactory connectionFactory() {
	// 	CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
	// 	connectionFactory.setHost(host);
	// 	connectionFactory.setPort(port);
	// 	connectionFactory.setUsername(username);
	// 	connectionFactory.setPassword(password);
	// 	return connectionFactory;
	// }
	//
	// @Bean
	// MessageConverter messageConverter() {
	// 	return new Jackson2JsonMessageConverter();
	// }
	//
	// @Bean
	// RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
	// 	RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
	// 	rabbitTemplate.setMessageConverter(messageConverter);
	// 	return rabbitTemplate;
	// }
}
