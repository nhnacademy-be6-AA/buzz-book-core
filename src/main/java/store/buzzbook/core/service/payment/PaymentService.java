package store.buzzbook.core.service.payment;

import static store.buzzbook.core.common.listener.OrderStatusListener.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.order.CouponStatusNotUpdatedException;
import store.buzzbook.core.common.exception.order.DuplicateBillLogException;
import store.buzzbook.core.common.exception.order.JSONParsingException;
import store.buzzbook.core.common.exception.order.ProductNotFoundException;
import store.buzzbook.core.common.exception.order.WrappingNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.coupon.CouponResponse;
import store.buzzbook.core.dto.coupon.UpdateCouponRequest;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.dto.payment.CreateBillLogRequest;
import store.buzzbook.core.dto.payment.CreateCancelBillLogRequest;
import store.buzzbook.core.dto.payment.ReadBillLogProjectionResponse;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.dto.payment.ReadBillLogsRequest;
import store.buzzbook.core.dto.payment.ReadPaymentResponse;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.coupon.CouponStatus;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.entity.payment.BillLog;
import store.buzzbook.core.entity.payment.BillStatus;
import store.buzzbook.core.entity.point.PointLog;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.mapper.order.OrderDetailMapper;
import store.buzzbook.core.mapper.order.OrderMapper;
import store.buzzbook.core.mapper.order.WrappingMapper;
import store.buzzbook.core.mapper.payment.BillLogMapper;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.order.WrappingRepository;
import store.buzzbook.core.repository.payment.BillLogRepository;
import store.buzzbook.core.repository.point.PointLogRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.point.PointService;
import store.buzzbook.core.service.user.UserService;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

	@Value("${api.gateway.host}")
	private String host;

	@Value("${api.gateway.port}")
	private int port;

	private static final String POINT_PAYMENT_INQUIRY = "주문 시 포인트 결제";
	private static final String POINT_CANCEL_INQUIRY = "취소 시 포인트 환불";
	private static final String POINT_REFUND_INQUIRY = "반품에 의한 포인트 환불";
	private static final String CANCEL_POINT_REFUND_INQUIRY = "반품에 의한 포인트 적립 취소";
	private static final String SIMPLE_PAYMENT = "간편결제";
	private static final String POINT = "POINT";
	private static final String PAYMENT_ERROR = "결제 오류";
	private static final String CANCEL_POINT_FOR_PAYMENT_ERROR_INQUIRY = "결제 오류로 인한 포인트 적립 취소";

	private final BillLogRepository billLogRepository;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final ObjectMapper objectMapper;
	private final WrappingRepository wrappingRepository;
	private final ProductRepository productRepository;
	private final OrderStatusRepository orderStatusRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final PointService pointService;
	private final PointLogRepository pointLogRepository;

	@Transactional(rollbackFor = Exception.class)
	public ReadBillLogResponse createBillLog(JSONObject billLogRequestObject) {
		ReadPaymentResponse readPaymentResponse = null;
		try {
			readPaymentResponse = objectMapper.convertValue(billLogRequestObject,
				ReadPaymentResponse.class);
		} catch (Exception e) {
			throw new JSONParsingException();
		}

		// 중복 체크
		if (billLogRepository.existsByPaymentAndPaymentKey(readPaymentResponse.getMethod(), readPaymentResponse.getPaymentKey())) {
			throw new DuplicateBillLogException();
		}

		Order order = orderRepository.findByOrderStr(readPaymentResponse.getOrderId());

		BillLog billLog = billLogRepository.save(
			BillLog.builder()
				.price(readPaymentResponse.getTotalAmount())
				.paymentKey(
					readPaymentResponse.getPaymentKey())
				.order(order)
				.status(BillStatus.valueOf(readPaymentResponse.getStatus()))
				.payment(readPaymentResponse.getMethod())
				.payAt(
					LocalDateTime.now())
				.build());
		UserInfo userInfo = null;
		if (order.getUser() != null) {
			userInfo = UserInfo.builder()
				.email(order.getOrderEmail())
				.loginId(order.getUser().getLoginId())
				.isAdmin(order.getUser().isAdmin())
				.contactNumber(order.getUser().getContactNumber())
				.birthday(order.getUser().getBirthday())
				.build();
		} else {
			userInfo = UserInfo.builder()
				.loginId(order.getOrderEmail())
				.build();
		}

		return BillLogMapper.toDto(billLog, OrderMapper.toDto(order, getReadOrderDetailResponses(order, PAID), userInfo.loginId()));
	}

	private List<ReadOrderDetailResponse> getReadOrderDetailResponses(Order order, String orderStatus) {
		List<ReadOrderDetailResponse> readOrderDetailResponses = new ArrayList<>();
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
		for (OrderDetail orderDetail : orderDetails) {
			orderDetail.setOrderStatus(orderStatusRepository.findByName(orderStatus));
			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(ProductNotFoundException::new);
			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId())
				.orElseThrow(WrappingNotFoundException::new);
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);
			readOrderDetailResponses.add(
				OrderDetailMapper.toDto(orderDetail, ProductResponse.convertToProductResponse(product),
					readWrappingResponse));
		}
		return readOrderDetailResponses;
	}

	@Transactional(rollbackFor = Exception.class)
	public ReadBillLogResponse createCancelBillLog(JSONObject billLogRequestObject) {

		ReadPaymentResponse readPaymentResponse = null;
		try {
			readPaymentResponse = objectMapper.convertValue(billLogRequestObject,
				ReadPaymentResponse.class);
		} catch (Exception e) {
			throw new JSONParsingException();
		}

		Order order = orderRepository.findByOrderStr(readPaymentResponse.getOrderId());

		BillLog billLog = billLogRepository.save(
			BillLog.builder()
				.price(Arrays.stream(readPaymentResponse.getCancels())
					.max(
						Comparator.comparing(cancel -> ZonedDateTime.parse(cancel.getCanceledAt(),
							DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
					.get()
					.getCancelAmount())
				.paymentKey(
					readPaymentResponse.getPaymentKey())
				.order(order)
				.status(BillStatus.valueOf(readPaymentResponse.getStatus()))
				.payment(readPaymentResponse.getMethod())
				.payAt(
					LocalDateTime.now())
				.cancelReason(Arrays.stream(readPaymentResponse.getCancels())
					.max(
						Comparator.comparing(cancel -> ZonedDateTime.parse(cancel.getCanceledAt(),
							DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
					.get()
					.getCancelReason())
				.build());

		UserInfo userInfo = UserInfo.builder()
			.email(order.getUser().getEmail())
			.loginId(order.getUser().getLoginId())
			.isAdmin(order.getUser().isAdmin())
			.contactNumber(order.getUser().getContactNumber())
			.birthday(order.getUser().getBirthday())
			.build();

		return BillLogMapper.toDto(billLog, OrderMapper.toDto(order, getReadOrderDetailResponses(order, CANCELED), userInfo.loginId()));
	}

	@Transactional(rollbackFor = Exception.class)
	public ReadBillLogResponse createBillLogWithDifferentPayment(CreateBillLogRequest createBillLogRequest,
		HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));

		Order order = orderRepository.findByOrderStr(createBillLogRequest.getOrderId());
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());

		List<ReadOrderDetailResponse> readOrderDetailResponses = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetails) {
			orderDetail.setOrderStatus(orderStatusRepository.findByName(PAID));
			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(ProductNotFoundException::new);
			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId())
				.orElseThrow(WrappingNotFoundException::new);
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);
			readOrderDetailResponses.add(
				OrderDetailMapper.toDto(orderDetail, ProductResponse.convertToProductResponse(product),
					readWrappingResponse));
		}

		BillLog billLog = billLogRepository.save(BillLogMapper.toEntity(createBillLogRequest, order));

		User user = userRepository.findByLoginId(userInfo.loginId())
			.orElseThrow(() -> new UserNotFoundException(userInfo.loginId()));

		if (createBillLogRequest.getPayment().equals(POINT)) {
			pointService.createPointLogWithDelta(user, POINT_PAYMENT_INQUIRY, -createBillLogRequest.getPrice());
		}

		if (!createBillLogRequest.getPayment().equals(SIMPLE_PAYMENT) && !createBillLogRequest.getPayment()
			.equals(POINT)) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set(AuthService.TOKEN_HEADER, request.getHeader(AuthService.TOKEN_HEADER));
			headers.set(AuthService.REFRESH_HEADER, request.getHeader(AuthService.REFRESH_HEADER));

			// 쿠폰 상태 업데이트 (재시도 포함)
			try {
				updateCouponStatus(billLog, headers, CouponStatus.USED);
			} catch (Exception e) {
				throw new CouponStatusNotUpdatedException();
			}
		}

		return BillLogMapper.toDto(billLog,
			OrderMapper.toDto(order, readOrderDetailResponses, user.getLoginId()));
	}

	@Retryable(
		retryFor = { CouponStatusNotUpdatedException.class },
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000)
	)
	protected void updateCouponStatus(BillLog billLog, HttpHeaders headers, CouponStatus couponStatus) {
		UpdateCouponRequest updateCouponRequest = new UpdateCouponRequest(billLog.getPayment(), couponStatus);
		HttpEntity<UpdateCouponRequest> updateCouponRequestHttpEntity = new HttpEntity<>(updateCouponRequest, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<CouponResponse> couponResponseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/coupons", host, port), HttpMethod.PUT, updateCouponRequestHttpEntity,
			CouponResponse.class);

		CouponResponse couponResponse = couponResponseResponseEntity.getBody();

		if (couponResponse == null || couponResponse.status() != couponStatus) {
			throw new CouponStatusNotUpdatedException();
		}
	}

	public Map<String, Object> readBillLogs(ReadBillLogsRequest request) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Page<ReadBillLogProjectionResponse> pageBillLogs = billLogRepository.findAll(request, pageable);
		List<ReadBillLogProjectionResponse> billLogs = pageBillLogs.getContent();

		data.put("responseData", billLogs);
		data.put("total", pageBillLogs.getTotalElements());

		return data;
	}

	public List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrder(long userId, String orderStr) {
		List<ReadBillLogWithoutOrderResponse> responses = new ArrayList<>();
		List<BillLog> billLogs = billLogRepository.findByUserIdAndOrderStr(userId, orderStr);

		for (BillLog newBillLog : billLogs) {
			responses.add(BillLogMapper.toDtoWithoutOrder(newBillLog));
		}

		return responses;
	}

	public List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrderWithAdmin(String orderId) {
		List<ReadBillLogWithoutOrderResponse> responses = new ArrayList<>();
		List<BillLog> billLogs = billLogRepository.findByOrder_OrderStr(orderId);

		for (BillLog newBillLog : billLogs) {
			responses.add(BillLogMapper.toDtoWithoutOrder(newBillLog));
		}

		return responses;
	}

	public List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrderWithoutLogin(String orderId) {
		List<ReadBillLogWithoutOrderResponse> responses = new ArrayList<>();
		List<BillLog> billLogs = billLogRepository.findByOrder_OrderStr(orderId);

		for (BillLog newBillLog : billLogs) {
			responses.add(BillLogMapper.toDtoWithoutOrder(newBillLog));
		}

		return responses;
	}

	public String getPaymentKeyWithoutLogin(String orderId, String orderEmail) {
		return billLogRepository.findByOrder_OrderStrAndOrder_OrderEmail(orderId, orderEmail)
			.getFirst()
			.getPaymentKey();
	}

	public String getPaymentKey(String orderId, long userId) {
		return billLogRepository.findByOrder_OrderStrAndOrder_User_Id(orderId, userId).getFirst().getPaymentKey();
	}

	@Transactional
	public void createCancelBillLogWithDifferentPayment(CreateCancelBillLogRequest createCancelBillLogRequest,
		HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));

		User user = userRepository.findByLoginId(userInfo.loginId())
			.orElseThrow(() -> new UserNotFoundException(userInfo.loginId()));
		List<BillLog> billLogs = billLogRepository.findAllByPaymentKey(createCancelBillLogRequest.getPaymentKey())
			.stream().filter(b -> !(b.getPayment().equals(SIMPLE_PAYMENT))).toList();

		for (BillLog billLog : billLogs) {
			billLogRepository.save(BillLog.builder()
				.price(billLog.getPrice())
				.paymentKey(createCancelBillLogRequest.getPaymentKey())
				.payment(billLog.getPayment())
				.status(createCancelBillLogRequest.getStatus())
				.order(billLog.getOrder())
				.cancelReason(createCancelBillLogRequest.getCancelReason())
				.payAt(LocalDateTime.now())
				.build());

			if (billLog.getPayment().equals(POINT)) {
				pointService.createPointLogWithDelta(user, POINT_CANCEL_INQUIRY, billLog.getPrice());
			}

			if (!billLog.getPayment().equals(SIMPLE_PAYMENT) && !billLog.getPayment().equals(POINT)) {
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", "application/json");

				try {
					updateCouponStatus(billLog, headers, CouponStatus.AVAILABLE);
				} catch (Exception e) {
					throw new CouponStatusNotUpdatedException();
				}
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void createRefundBillLogWithDifferentPayment(CreateCancelBillLogRequest createCancelBillLogRequest,
		HttpServletRequest request) {

		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));

		User user = userRepository.findByLoginId(userInfo.loginId())
			.orElseThrow(() -> new UserNotFoundException(userInfo.loginId()));
		List<BillLog> billLogs = billLogRepository.findAllByPaymentKey(createCancelBillLogRequest.getPaymentKey());

		for (BillLog billLog : billLogs) {
			billLogRepository.save(BillLog.builder()
				.price(billLog.getPrice())
				.paymentKey(createCancelBillLogRequest.getPaymentKey())
				.payment(billLog.getPayment())
				.status(createCancelBillLogRequest.getStatus())
				.order(billLog.getOrder())
				.cancelReason(createCancelBillLogRequest.getCancelReason())
				.payAt(LocalDateTime.now())
				.build());

			if (billLog.getPayment().equals(SIMPLE_PAYMENT)) {
				int deliveryRate = billLog.getOrder().getDeliveryRate();

				pointService.createPointLogWithDelta(user, POINT_REFUND_INQUIRY, billLog.getPrice() - deliveryRate);
			}

			if (billLog.getPayment().equals(POINT)) {
				pointService.createPointLogWithDelta(user, CANCEL_POINT_REFUND_INQUIRY, -billLog.getPrice());
			}

			if (!billLog.getPayment().equals(SIMPLE_PAYMENT) && !billLog.getPayment().equals(POINT)) {
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", "application/json");

				try {
					updateCouponStatus(billLog, headers, CouponStatus.AVAILABLE);
				} catch (Exception e) {
					throw new CouponStatusNotUpdatedException();
				}

			}
		}
	}

	@Transactional
	public void rollbackBillLog(ReadPaymentResponse readPaymentResponse) {
		List<BillLog> billLogs = billLogRepository.findAllByPaymentKey(readPaymentResponse.getPaymentKey());
		for (BillLog billLog : billLogs) {
			billLogRepository.save(BillLog.builder().payment(billLog.getPayment()).price(billLog.getPrice())
				.payAt(LocalDateTime.now()).status(BillStatus.CANCELED).paymentKey(billLog.getPaymentKey()).order(billLog.getOrder()).cancelReason(PAYMENT_ERROR).build());
		}

		User user = billLogs.getFirst().getOrder().getUser();
		PointLog pointLog = pointLogRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId());
		pointService.createPointLogWithDelta(user, CANCEL_POINT_FOR_PAYMENT_ERROR_INQUIRY, -pointLog.getDelta());
	}
}
