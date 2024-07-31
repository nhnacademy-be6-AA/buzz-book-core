package store.buzzbook.core.service.order;

import static store.buzzbook.core.common.listener.OrderStatusListener.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import store.buzzbook.core.common.exception.order.CouponStatusNotUpdatedException;
import store.buzzbook.core.common.exception.order.OrderNotFoundException;
import store.buzzbook.core.common.exception.order.ProductOutOfStockException;
import store.buzzbook.core.dto.coupon.CouponResponse;
import store.buzzbook.core.dto.coupon.UpdateCouponRequest;
import store.buzzbook.core.entity.coupon.CouponStatus;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.payment.BillLog;
import store.buzzbook.core.entity.payment.BillStatus;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.payment.BillLogRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.service.point.PointService;

@Service
public class UserOrderCancelService extends AbstractOrderCancelService {
	@Value("${api.gateway.host}")
	private String host;
	@Value("${api.gateway.port}")
	private int port;

	private static final String POINT = "POINT";
	private static final String USEPOINT_CANCEL_INQUIRY = "결제 취소 시 포인트 사용 취소";
	private static final String CANCEL_EARNED_POINT_INQUIRY = "결제 취소에 의한 포인트 적립 취소";

	private PointService pointService;
	private BillLogRepository billLogRepository;

	protected UserOrderCancelService(OrderRepository orderRepository,
		OrderStatusRepository orderStatusRepository, ProductRepository productRepository, PointService pointService,
		BillLogRepository billLogRepository) {
		super(orderRepository, orderStatusRepository, productRepository);
		this.pointService = pointService;
		this.billLogRepository = billLogRepository;
	}

	void cancelPoints(Order order, long userId, int cancelPoints, String paymentKey) {
		pointService.createPointLogWithDelta(userId, USEPOINT_CANCEL_INQUIRY, cancelPoints);

		billLogRepository.save(
			BillLog.builder()
				.price(cancelPoints)
				.paymentKey(
					paymentKey)
				.order(order)
				.status(BillStatus.DONE)
				.payment(POINT)
				.payAt(
					LocalDateTime.now())
				.build());
	}

	void cancelCoupon(Order order, long userId, String couponCode, int deductedCouponPrice, String paymentKey, HttpHeaders headers) {
		UpdateCouponRequest updateCouponRequest = new UpdateCouponRequest(couponCode, CouponStatus.AVAILABLE);
		HttpEntity<UpdateCouponRequest> updateCouponRequestHttpEntity = new HttpEntity<>(updateCouponRequest, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<CouponResponse> couponResponseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/coupons", host, port), HttpMethod.PUT, updateCouponRequestHttpEntity,
			CouponResponse.class);

		CouponResponse couponResponse = couponResponseResponseEntity.getBody();

		if (couponResponse == null || couponResponse.status() != CouponStatus.USED) {
			updateCouponStatus(couponCode, headers);
		}

		billLogRepository.save(
			BillLog.builder()
				.price(deductedCouponPrice)
				.paymentKey(
					paymentKey)
				.order(order)
				.status(BillStatus.DONE)
				.payment(couponCode)
				.payAt(
					LocalDateTime.now())
				.build());
	}

	@Retryable(
		retryFor = { CouponStatusNotUpdatedException.class },
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000)
	)
	private void updateCouponStatus(String couponCode, HttpHeaders headers) {
		UpdateCouponRequest updateCouponRequest = new UpdateCouponRequest(couponCode, CouponStatus.AVAILABLE);
		HttpEntity<UpdateCouponRequest> updateCouponRequestHttpEntity = new HttpEntity<>(updateCouponRequest, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<CouponResponse> couponResponseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/coupons", host, port), HttpMethod.PUT, updateCouponRequestHttpEntity,
			CouponResponse.class);

		CouponResponse couponResponse = couponResponseResponseEntity.getBody();

		if (couponResponse == null || couponResponse.status() != CouponStatus.AVAILABLE) {
			throw new CouponStatusNotUpdatedException();
		}
	}

	void cancelEarnedPoints(long userId, int earnedPoints) {
		pointService.createPointLogWithDelta(userId, CANCEL_EARNED_POINT_INQUIRY, earnedPoints);
	}

	public void process(long orderId, String paymentKey, HttpHeaders headers) {
		OrderStatus orderStatus = orderStatusRepository.findByName(CANCELED);

		Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
		List<OrderDetail> details = order.getDetails();

		for (OrderDetail detail : details) {
			Product product = detail.getProduct();
			// 1. 검증

			// 2. 재고 처리
			increaseStock(product.getId(), detail.getQuantity());
		}

		cancelPoints(order, order.getUser().getId(), order.getDeductedPoints(), paymentKey);
		cancelCoupon(order, order.getUser().getId(), order.getCouponCode(), order.getDeductedCouponPrice(), paymentKey, headers);
		cancelEarnedPoints(order.getUser().getId(), order.getEarnedPoints());
		updateOrderStatus(orderId, orderStatus);
	}

	@Override
	public void nonUserProcess(long orderId) {
		return;
	}
}