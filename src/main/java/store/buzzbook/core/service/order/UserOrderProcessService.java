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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import store.buzzbook.core.common.exception.order.CouponStatusNotUpdatedException;
import store.buzzbook.core.common.exception.order.OrderNotFoundException;
import store.buzzbook.core.common.exception.order.OutOfCouponException;
import store.buzzbook.core.common.exception.order.OutOfPointsException;
import store.buzzbook.core.common.exception.order.ProductNotFoundException;
import store.buzzbook.core.common.exception.order.ProductOutOfStockException;
import store.buzzbook.core.dto.coupon.CouponRequest;
import store.buzzbook.core.dto.coupon.CouponResponse;
import store.buzzbook.core.dto.coupon.CouponStatusResponse;
import store.buzzbook.core.dto.coupon.UpdateCouponRequest;
import store.buzzbook.core.dto.payment.PayInfo;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.coupon.CouponStatus;
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
import store.buzzbook.core.repository.point.PointPolicyRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.service.point.PointService;
import store.buzzbook.core.service.user.UserService;

@Service
public class UserOrderProcessService extends AbstractOrderProcessService {
	@Value("${api.gateway.host}")
	private String host;
	@Value("${api.gateway.port}")
	private int port;

	private static final String POINT = "POINT";
	private static final String POINT_PAYMENT_INQUIRY = "주문 시 포인트 결제";
	private static final String POINT_EARN_INQUIRY = "주문 시 포인트 적립";
	private static final String ORDER_BOOK_POINT_POLICY = "전체도서";

	private PointService pointService;
	private UserService userService;
	private PointPolicyRepository pointPolicyRepository;
	private BillLogRepository billLogRepository;

	protected UserOrderProcessService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository,
		ProductRepository productRepository, PointService pointService, UserService userService, PointPolicyRepository pointPolicyRepository,
		BillLogRepository billLogRepository) {
		super(orderRepository, orderStatusRepository, productRepository);
		this.pointService = pointService;
		this.userService = userService;
		this.pointPolicyRepository = pointPolicyRepository;
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
		CouponRequest couponRequest = new CouponRequest(couponCode);
		HttpEntity<CouponRequest> couponRequestHttpEntity = new HttpEntity<>(couponRequest, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<CouponStatusResponse> couponResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/coupons/info", host, port), HttpMethod.POST, couponRequestHttpEntity,
			CouponStatusResponse.class);

		if (CouponStatus.AVAILABLE == CouponStatus.fromString(couponResponseEntity.getBody().status())) {
			return false;
		} else {
			return true;
		}
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

	void usePoints(Order order, long userId, int usePoints, String paymentKey) {
		pointService.createPointLogWithDelta(userId, POINT_PAYMENT_INQUIRY, -usePoints);

		billLogRepository.save(
			BillLog.builder()
				.price(usePoints)
				.paymentKey(
					paymentKey)
				.order(order)
				.status(BillStatus.DONE)
				.payment(POINT)
				.payAt(
					LocalDateTime.now())
				.build());
	}

	void useCoupon(Order order, long userId, String couponCode, int deductedCouponPrice, String paymentKey, HttpHeaders headers) {
		UpdateCouponRequest updateCouponRequest = new UpdateCouponRequest(couponCode, CouponStatus.USED);
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
		UpdateCouponRequest updateCouponRequest = new UpdateCouponRequest(couponCode, CouponStatus.USED);
		HttpEntity<UpdateCouponRequest> updateCouponRequestHttpEntity = new HttpEntity<>(updateCouponRequest, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<CouponResponse> couponResponseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/coupons", host, port), HttpMethod.PUT, updateCouponRequestHttpEntity,
			CouponResponse.class);

		CouponResponse couponResponse = couponResponseResponseEntity.getBody();

		if (couponResponse == null || couponResponse.status() != CouponStatus.USED) {
			throw new CouponStatusNotUpdatedException();
		}
	}

	void earnPoints(long orderId, long userId, int earnPoints) {
		if (earnPoints < 0) {
			earnPoints = 0;
		}

		Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
		UserInfo userInfo = userService.getUserInfoByUserId(userId);
		double pointRate = pointPolicyRepository.findByName(ORDER_BOOK_POINT_POLICY).getRate();
		int benefit = (int)(earnPoints * userInfo.grade().benefit());
		int point = (int)(earnPoints * pointRate);
		pointService.createPointLogWithDelta(userId, POINT_EARN_INQUIRY, point+benefit);
		order.setEarnedPoints(point+benefit);
	}

	// @Override
	// void notify(long orderId, long userId, String message) {
	//
	// }

	@Override
	public void process(long orderId, PayInfo payInfo, HttpHeaders headers) {
		OrderStatus orderStatus = orderStatusRepository.findByName(PAID);

		Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
		List<OrderDetail> details = order.getDetails();

		if (validatePoints(order.getDeductedPoints(), pointService.getUserPoint(order.getUser().getId()))) {
			throw new OutOfPointsException();
		}

		if (order.getCouponCode() != null) {
			if (validateCoupon(order.getUser(), order.getCouponCode(), headers)) {
				throw new OutOfCouponException();
			}
		}

		for (OrderDetail detail : details) {
			Product product = detail.getProduct();
			// 1. 검증
			if (validateStock(product.getId(), detail.getQuantity())) {
				throw new ProductOutOfStockException();
			}

			// 2. 재고 처리
			decreaseStock(product.getId(), detail.getQuantity());
		}

		// 3. 결제
		if (order.getPrice() != 0) {
			savePayment(order, payInfo);
		}


		// 4. 포인트 사용
		if (order.getDeductedPoints() != 0) {
			usePoints(order, order.getUser().getId(), order.getDeductedPoints(), payInfo.getPaymentKey());
		}

		// 5. 쿠폰 사용
		if (order.getCouponCode() != null) {
			useCoupon(order, order.getUser().getId(), order.getCouponCode(), order.getDeductedCouponPrice(), payInfo.getPaymentKey(), headers);
		}
		// 6. 포인트 적립
		earnPoints(orderId, order.getUser().getId(), order.getPrice() - order.getDeliveryRate());
		// 7. 주문 상태 변경
		updateOrderStatus(order.getId(), orderStatus);
		// 8. 고객 알림
		// notify(order.getId(), order.getUser().getId(), "Order successful");
	}

	@Override
	public void nonUserProcess(long orderId, PayInfo payInfo) {
		return;
	}
}
