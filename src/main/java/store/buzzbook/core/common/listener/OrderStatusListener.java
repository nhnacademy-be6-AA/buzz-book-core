package store.buzzbook.core.common.listener;

import java.time.LocalDateTime;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.repository.order.OrderStatusRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderStatusListener implements ApplicationListener<ApplicationReadyEvent> {
	private static final String READY = "READY";
	private static final String CANCELED = "CANCELED";
	private static final String PARTIAL_CANCELED = "PARTIAL_CANCELED";
	private static final String WAITING_FOR_DEPOSIT = "WAITING_FOR_DEPOSIT";
	private static final String REFUND = "REFUND";
	private static final String PARTIAL_REFUND = "PARTIAL_REFUND";
	private static final String SHIPPING_OUT = "SHIPPING_OUT";
	private static final String SHIPPED = "SHIPPED";
	private static final String BREAKAGE_REFUND = "BREAKAGE_REFUND";


	private final OrderStatusRepository orderStatusRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (!orderStatusRepository.existsByName(READY)) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(READY)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (!orderStatusRepository.existsByName(CANCELED)) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(CANCELED)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (!orderStatusRepository.existsByName(PARTIAL_CANCELED)) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(PARTIAL_CANCELED)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (!orderStatusRepository.existsByName(WAITING_FOR_DEPOSIT)) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(WAITING_FOR_DEPOSIT)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (!orderStatusRepository.existsByName(REFUND)) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(REFUND)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (!orderStatusRepository.existsByName(PARTIAL_REFUND)) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(PARTIAL_REFUND)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (!orderStatusRepository.existsByName(SHIPPING_OUT)) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(SHIPPING_OUT)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (!orderStatusRepository.existsByName(SHIPPED)) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(SHIPPED)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (!orderStatusRepository.existsByName(BREAKAGE_REFUND)) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(BREAKAGE_REFUND)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}
	}
}
