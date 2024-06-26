package store.buzzbook.core.common.service.impl;

import static store.buzzbook.core.common.config.RabbitmqConfig.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.service.CouponProducerService;
import store.buzzbook.core.dto.coupon.CreateWelcomeCouponRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponProducerServiceImpl implements CouponProducerService {

	private final RabbitTemplate rabbitTemplate;

	@Override
	public void sendWelcomeCouponRequest(CreateWelcomeCouponRequest request) {

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String objectToJson = objectMapper.writeValueAsString(request);
			rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, TOPIC_ROUTING_KEY, objectToJson);
		} catch (JsonProcessingException e) {
			log.error("파싱 오류 발생");
		}
	}
}
