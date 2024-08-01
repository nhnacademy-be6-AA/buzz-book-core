package store.buzzbook.core.service.order;

import static store.buzzbook.core.common.listener.OrderStatusListener.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import store.buzzbook.core.common.exception.order.AlreadyCanceledException;
import store.buzzbook.core.common.exception.order.AlreadyRefundedException;
import store.buzzbook.core.common.exception.order.NotPaidException;
import store.buzzbook.core.common.exception.order.OrderNotFoundException;
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
public class NonUserOrderCancelService extends AbstractOrderCancelService {
	private BillLogRepository billLogRepository;

	protected NonUserOrderCancelService(OrderRepository orderRepository,
		OrderStatusRepository orderStatusRepository,
		ProductRepository productRepository, BillLogRepository billLogRepository) {
		super(orderRepository, orderStatusRepository, productRepository);
		this.billLogRepository = billLogRepository;
	}

	@Override
	boolean validateOrderStatus(Order order) {
		if (order.getOrderStatus().equals(orderStatusRepository.findByName(CANCELED))) {
			throw new AlreadyCanceledException();
		}
		if (order.getOrderStatus().equals(orderStatusRepository.findByName(REFUND)) || order.getOrderStatus().equals(orderStatusRepository.findByName(BREAKAGE_REFUND))) {
			throw new AlreadyRefundedException();
		}
		if (!order.getOrderStatus().equals(orderStatusRepository.findByName(PAID))) {
			throw new NotPaidException();
		}

		return false;
	}

	@Override
	boolean validateCoupon(User user, String couponCode, HttpHeaders headers) {
		return false;
	}

	void saveCancelPayment(Order order, PayInfo payInfo) {
		billLogRepository.save(
			BillLog.builder()
				.price(payInfo.getPrice())
				.paymentKey(
					payInfo.getPaymentKey())
				.order(order)
				.status(BillStatus.CANCELED)
				.payment(payInfo.getPayType().name())
				.payAt(
					LocalDateTime.now())
				.build());
	}

	public void nonUserProcess(long orderId, PayInfo payInfo) {
		OrderStatus orderStatus = orderStatusRepository.findByName(CANCELED);

		Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
		List<OrderDetail> details = order.getDetails();

		validateOrderStatus(order);

		for (OrderDetail detail : details) {
			Product product = detail.getProduct();
			// 1. 검증

			// 2. 재고 처리
			increaseStock(product.getId(), detail.getQuantity());
		}

		saveCancelPayment(order, payInfo);

		// 6. 주문 상태 변경
		updateOrderStatus(order.getId(), orderStatus);
	}

	@Override
	public void process(long orderId, PayInfo payInfo, HttpHeaders headers) {
		return;
	}
}
