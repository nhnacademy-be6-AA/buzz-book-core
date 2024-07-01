package store.buzzbook.core.common.service.impl;

import static store.buzzbook.core.common.config.RabbitmqConfig.*;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.service.UserProducerService;
import store.buzzbook.core.dto.coupon.CreateWelcomeCouponRequest;
import store.buzzbook.core.dto.coupon.DownloadCouponRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProducerServiceImpl implements UserProducerService {

	private final RabbitTemplate rabbitTemplate;

	@Override
	public void sendWelcomeCouponRequest(CreateWelcomeCouponRequest request) {
		Message message = MessageBuilder.withBody(serialize(request))
			.setContentType(MessageProperties.CONTENT_TYPE_JSON)
			.setHeader("Content-Type", "application/json")
			.setHeader("type", "welcome")
			.build();
		rabbitTemplate.convertAndSend(REQUEST_EXCHANGE_NAME, REQUEST_ROUTING_KEY, message);
	}

	@Override
	public void sendDownloadCouponRequest(DownloadCouponRequest request) {
		Message message = MessageBuilder.withBody(serialize(request))
			.setContentType(MessageProperties.CONTENT_TYPE_JSON)
			.setHeader("Content-Type", "application/json")
			.setHeader("type", "download")
			.build();
		rabbitTemplate.convertAndSend(REQUEST_EXCHANGE_NAME, REQUEST_ROUTING_KEY, message);
	}

	private byte[] serialize(Object obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsBytes(obj);
		} catch (Exception e) {
			throw new RuntimeException("Serialization error", e);
		}
	}
}
