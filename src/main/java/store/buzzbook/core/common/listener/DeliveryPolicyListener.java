package store.buzzbook.core.common.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.repository.order.DeliveryPolicyRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryPolicyListener implements ApplicationListener<ApplicationReadyEvent> {
	public static final String BASIC = "기본";
	public static final String MOUNTAINOUS = "도서산간";

	private final DeliveryPolicyRepository deliveryPolicyRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (Boolean.FALSE.equals(deliveryPolicyRepository.existsByName(BASIC))) {
			DeliveryPolicy deliveryPolicy = DeliveryPolicy.builder()
				.name(BASIC)
				.standardPrice(20000)
				.policyPrice(0)
				.deleted(false)
				.build();
			deliveryPolicyRepository.save(deliveryPolicy);
		}

		if (Boolean.FALSE.equals(deliveryPolicyRepository.existsByName(MOUNTAINOUS))) {
			DeliveryPolicy deliveryPolicy = DeliveryPolicy.builder()
				.name(MOUNTAINOUS)
				.standardPrice(30000)
				.policyPrice(0)
				.deleted(false)
				.build();
			deliveryPolicyRepository.save(deliveryPolicy);
		}
	}
}
