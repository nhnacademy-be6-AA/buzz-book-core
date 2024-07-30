package store.buzzbook.core.service.order;

import static store.buzzbook.core.common.listener.OrderStatusListener.*;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import store.buzzbook.core.common.exception.order.OrderNotFoundException;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.product.ProductRepository;

@Service
public class NonUserOrderProcessService extends AbstractOrderProcessService {
	protected NonUserOrderProcessService(OrderRepository orderRepository,
		OrderStatusRepository orderStatusRepository, ProductRepository productRepository) {
		super(orderRepository, orderStatusRepository, productRepository);
	}

	@Override
	boolean validateStock(int productId, int quantity) {
		return false;
	}

	// @Override
	// void notify(long orderId, long userId, String message) {
	//
	// }

	public void nonUserProcess(long orderId, HttpHeaders headers) {
		OrderStatus orderStatus = orderStatusRepository.findByName(PAID);

		Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
		List<OrderDetail> details = order.getDetails();

		for (OrderDetail detail : details) {
			Product product = detail.getProduct();
			// 1. 검증
			if (validateStock(product.getId(), detail.getQuantity())) {
				// 예외 처리 하겠다.
			}
			// 2. 재고 처리
			decreaseStock(product.getId(), detail.getQuantity());
		}
		// 6. 주문 상태 변경
		updateOrderStatus(order.getId(), orderStatus);
		// 7. 고객 알림
		// notify(order.getId(), order.getUser().getId(), "Order successful");
	}

	@Override
	public void process(long orderId, String paymentKey, HttpHeaders headers) {
		return;
	}
}
