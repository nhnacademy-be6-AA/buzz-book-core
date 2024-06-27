package store.buzzbook.core.common.service.impl;

import static store.buzzbook.core.common.config.RabbitmqConfig.*;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.coupon.CreateWelcomeCouponResponse;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.user.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl {

	private final UserRepository userRepository;

	@Transactional
	@RabbitListener(queues = RESPONSE_QUEUE_NAME)
	public void receiveWelcomeCouponResponse(CreateWelcomeCouponResponse response) {
		log.info("Received Welcome Coupon response from RabbitMQ : {}", response.toString());

		User user = userRepository.findById(response.userId())
			.orElseThrow(UserNotFoundException::new);

		user.getCoupons().add(response.couponId());
	}
}
