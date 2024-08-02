package store.buzzbook.core.service.order;

import org.springframework.http.HttpHeaders;

import store.buzzbook.core.common.exception.order.OrderNotFoundException;
import store.buzzbook.core.common.exception.order.ProductNotFoundException;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.product.ProductRepository;

public abstract class AbstractOrderProcessService implements OrderStrategy {
	protected OrderRepository orderRepository;
	protected OrderStatusRepository orderStatusRepository;
	protected ProductRepository productRepository;

	protected AbstractOrderProcessService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository,
		ProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.orderStatusRepository = orderStatusRepository;
		this.productRepository = productRepository;
	}

	// - 회원해당
	// - Validation (재고, 포인트, 쿠폰)
	// - 재고처리
	// - 결제
	// - 포인트 사용 ( 주문시 사용한 포인트 처리)
	// - 쿠폰 사용(적용)
	// - 결제 처리(결제 내역생성)
	// - 포인트 적립 ( 주문 완료 후 결제 금액에 대한 포인트 적립)
	// - 주문 상태 변경
	// - 고객 알림 ( 문자, 카카오 메시지)

	// 검증
	abstract boolean validateStock(int productId, int quantity);
	abstract boolean validatePoints(int deductedPoints, int holdingPoints);
	abstract boolean validateCoupon(User user, String couponCode, HttpHeaders headers);

	// 재고 처리
	void decreaseStock(int productId, int quantity) {
		Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
		product.decreaseStock(quantity);
	}
	// 주문 상태 변경
	void updateOrderStatus(long orderId, OrderStatus orderStatus) {
		Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
		order.changeOrderStatus(orderStatus);
	}

	// 고객 알림
	// abstract void notify(long orderId, long userId, String message);

}
