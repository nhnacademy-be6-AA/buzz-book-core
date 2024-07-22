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
	public static final String READY = "READY";
	public static final String CANCELED = "CANCELED";
	public static final String PARTIAL_CANCELED = "PARTIAL_CANCELED";
	public static final String WAITING_FOR_DEPOSIT = "WAITING_FOR_DEPOSIT";
	public static final String REFUND = "REFUND";
	public static final String PARTIAL_REFUND = "PARTIAL_REFUND";
	public static final String SHIPPING_OUT = "SHIPPING_OUT";
	public static final String SHIPPED = "SHIPPED";
	public static final String BREAKAGE_REFUND = "BREAKAGE_REFUND";
	public static final String PAID = "PAID";

	private final OrderStatusRepository orderStatusRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (Boolean.FALSE.equals(orderStatusRepository.existsByName(READY))) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(READY)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (Boolean.FALSE.equals(orderStatusRepository.existsByName(CANCELED))) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(CANCELED)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (Boolean.FALSE.equals(orderStatusRepository.existsByName(PARTIAL_CANCELED))) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(PARTIAL_CANCELED)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (Boolean.FALSE.equals(orderStatusRepository.existsByName(WAITING_FOR_DEPOSIT))) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(WAITING_FOR_DEPOSIT)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (Boolean.FALSE.equals(orderStatusRepository.existsByName(REFUND))) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(REFUND)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (Boolean.FALSE.equals(orderStatusRepository.existsByName(PARTIAL_REFUND))) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(PARTIAL_REFUND)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (Boolean.FALSE.equals(orderStatusRepository.existsByName(SHIPPING_OUT))) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(SHIPPING_OUT)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (Boolean.FALSE.equals(orderStatusRepository.existsByName(SHIPPED))) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(SHIPPED)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (Boolean.FALSE.equals(orderStatusRepository.existsByName(BREAKAGE_REFUND))) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(BREAKAGE_REFUND)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}

		if (Boolean.FALSE.equals(orderStatusRepository.existsByName(PAID))) {
			OrderStatus orderStatus = OrderStatus.builder()
				.name(PAID)
				.updateAt(LocalDateTime.now())
				.build();
			orderStatusRepository.save(orderStatus);
		}
	}
}
