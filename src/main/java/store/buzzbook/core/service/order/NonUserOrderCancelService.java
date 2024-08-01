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
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.product.ProductRepository;

@Service
public class NonUserOrderCancelService extends AbstractOrderCancelService {

	protected NonUserOrderCancelService(OrderRepository orderRepository,
		OrderStatusRepository orderStatusRepository,
		ProductRepository productRepository) {
		super(orderRepository, orderStatusRepository, productRepository);
	}

	@Override
	boolean validateCoupon(User user, String couponCode, HttpHeaders headers) {
		return false;
	}

	public void nonUserProcess(long orderId) {
		OrderStatus orderStatus = orderStatusRepository.findByName(CANCELED);

		Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
		List<OrderDetail> details = order.getDetails();

		for (OrderDetail detail : details) {
			Product product = detail.getProduct();
			// 1. 검증

			// 2. 재고 처리
			increaseStock(product.getId(), detail.getQuantity());
		}
		// 6. 주문 상태 변경
		updateOrderStatus(order.getId(), orderStatus);
	}

	@Override
	public void process(long orderId, String paymentKey, HttpHeaders headers) {
		return;
	}
}
