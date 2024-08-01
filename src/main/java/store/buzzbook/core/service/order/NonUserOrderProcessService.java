package store.buzzbook.core.service.order;

import static store.buzzbook.core.common.listener.OrderStatusListener.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import store.buzzbook.core.common.exception.order.OrderNotFoundException;
import store.buzzbook.core.common.exception.order.ProductNotFoundException;
import store.buzzbook.core.common.exception.order.ProductOutOfStockException;
import store.buzzbook.core.dto.payment.PayInfo;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.payment.BillLog;
import store.buzzbook.core.entity.payment.BillStatus;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.payment.BillLogRepository;
import store.buzzbook.core.repository.product.ProductRepository;

@Service
public class NonUserOrderProcessService extends AbstractOrderProcessService {
	private BillLogRepository billLogRepository;

	protected NonUserOrderProcessService(OrderRepository orderRepository,
		OrderStatusRepository orderStatusRepository, ProductRepository productRepository, BillLogRepository billLogRepository) {
		super(orderRepository, orderStatusRepository, productRepository);
		this.billLogRepository = billLogRepository;
	}

	@Override
	boolean validateStock(int productId, int quantity) {
		Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
		return (product.getStock() < quantity);
	}

	@Override
	boolean validatePoints(int deductedPoints, int holdingPoints) {
		return (deductedPoints > holdingPoints);
	}

	@Override
	boolean validateCoupon(User user, String couponCode, HttpHeaders headers) {
		return false;
	}

	void savePayment(Order order, PayInfo payInfo) {
		billLogRepository.save(
			BillLog.builder()
				.price(payInfo.getPrice())
				.paymentKey(
					payInfo.getPaymentKey())
				.order(order)
				.status(BillStatus.DONE)
				.payment(payInfo.getPayType().name())
				.payAt(
					LocalDateTime.now())
				.build());
	}

	// @Override
	// void notify(long orderId, long userId, String message) {
	//
	// }

	public void nonUserProcess(long orderId, PayInfo payInfo) {
		OrderStatus orderStatus = orderStatusRepository.findByName(PAID);

		Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
		List<OrderDetail> details = order.getDetails();

		for (OrderDetail detail : details) {
			Product product = detail.getProduct();
			// 1. 검증
			if (validateStock(product.getId(), detail.getQuantity())) {
				throw new ProductOutOfStockException();
			}

			// 2. 재고 처리
			decreaseStock(product.getId(), detail.getQuantity());
		}

		savePayment(order, payInfo);

		// 6. 주문 상태 변경
		updateOrderStatus(order.getId(), orderStatus);
		// 7. 고객 알림
		// notify(order.getId(), order.getUser().getId(), "Order successful");
	}

	@Override
	public void process(long orderId, PayInfo payInfo, HttpHeaders headers) {
		return;
	}
}
