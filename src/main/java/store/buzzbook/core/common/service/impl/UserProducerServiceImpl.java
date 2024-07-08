package store.buzzbook.core.common.service.impl;

import static store.buzzbook.core.common.config.RabbitmqConfig.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.service.UserProducerService;
import store.buzzbook.core.dto.coupon.CreateWelcomeCouponRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProducerServiceImpl implements UserProducerService {

	private final RabbitTemplate rabbitTemplate;

	@Override
	public void sendWelcomeCouponRequest(CreateWelcomeCouponRequest request) {
		rabbitTemplate.convertAndSend(REQUEST_EXCHANGE_NAME, REQUEST_ROUTING_KEY, request);
	}
}
