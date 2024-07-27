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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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
import store.buzzbook.core.dto.order.ReadOrderWithBillLogsResponse;
import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.dto.payment.CreateBillLogRequest;
import store.buzzbook.core.dto.payment.CreateCancelBillLogRequest;
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

/**
 * 결제 관련 서비스
 * 결제 내역 조회, 생성, paymentKey 조회, 취소와 환불 내역 생성, 결제 내역 롤백 기능을 제공합니다.
 *
 * @author 박설
 */

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

	/**
	 *  결제 내역을 생성합니다.
	 *
	 * @param billLogRequestObject 토스 api에서 반환하는 payment 객체
	 * @return 결제 내역 응답 객체
	 */

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

	/**
	 * 주문 번호로 조회한 주문 상세 리스트들의 상태를 수정하고 DTO 리스트로 반환합니다.
	 *
	 * @param order 주문 엔터티
	 * @param orderStatus 주문 상태 이름
	 * @return 주문 상세 응답 객체 리스트
	 */

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

	/**
	 * 주문 취소 내역을 생성합니다.
	 *
	 * @param billLogRequestObject 토스 api에서 반환하는 payment 객체
	 * @return 주문 내역 응답 객체
	 */

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

		if (order.getUser() != null) {
			return BillLogMapper.toDto(billLog, OrderMapper.toDto(order, getReadOrderDetailResponses(order, CANCELED), order.getUser().getLoginId()));
		}

		return BillLogMapper.toDto(billLog, OrderMapper.toDto(order, getReadOrderDetailResponses(order, CANCELED), null));
	}

	/**
	 * 다른 지불 수단(쿠폰, 포인트)에 대한 결제 내역을 생성합니다.
	 *
	 * @param createBillLogRequest 결제 내역 생성 요청 객체
	 * @param request 고객 정보를 얻기 위한 HttpServletRequest 객체
	 * @return 결제 내역 응답 객체
	 */

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

	/**
	 * 쿠폰 상태를 업데이트 합니다.
	 *
	 * @param billLog 결제 내역 엔터티
	 * @param headers 쿠폰 서버로 보낼 고객 인증 헤더
	 * @param couponStatus 쿠폰 상태 객체
	 * @throws CouponStatusNotUpdatedException 쿠폰 상태가 업데이트 되지 않았을 경우 2초마다 최대 3번 재시도 요청
	 */

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

	/**
	 * 결제 내역과 함께 주문 내역들을 조회합니다.
	 *
	 * @param request 결제 내역 조회 요청 객체
	 * @return 결제 내역이 딸린 주문 내역 리스트 객체와 다음 페이지 존재 여부를 가진 Map 객체
	 */

	@Transactional(readOnly = true)
	public Map<String, Object> readBillLogs(ReadBillLogsRequest request) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Slice<ReadOrderWithBillLogsResponse> pageBillLogs = orderRepository.readOrdersWithBillLogs(request, pageable);

		data.put("responseData", pageBillLogs.getContent());
		data.put("hasNext", pageBillLogs.hasNext());

		return data;
	}

	/**
	 * 주문 정보 없이 결제 내역을 조회합니다.
	 *
	 * @param userId 고객 번호
	 * @param orderStr 주문 문자열(코드)
	 * @return 주문 정보가 없는 결제 내역 응답 객체 리스트
	 */

	@Transactional(readOnly = true)
	public List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrder(long userId, String orderStr) {
		List<ReadBillLogWithoutOrderResponse> responses = new ArrayList<>();
		List<BillLog> billLogs = billLogRepository.findByUserIdAndOrderStr(userId, orderStr);

		for (BillLog newBillLog : billLogs) {
			responses.add(BillLogMapper.toDtoWithoutOrder(newBillLog));
		}

		return responses;
	}

	/**
	 * 관리자가 주문 정보 없이 결제 내역을 조회합니다.
	 *
	 * @param orderId 주문 문자열(코드)
	 * @return 주문 정보가 없는 결제 내역 응답 객체 리스트
	 */

	@Transactional(readOnly = true)
	public List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrderWithAdmin(String orderId) {
		List<ReadBillLogWithoutOrderResponse> responses = new ArrayList<>();
		List<BillLog> billLogs = billLogRepository.findByOrder_OrderStr(orderId);

		for (BillLog newBillLog : billLogs) {
			responses.add(BillLogMapper.toDtoWithoutOrder(newBillLog));
		}

		return responses;
	}

	/**
	 * 비회원이 주문 정보없이 결제 내역을 조회합니다.
	 *
	 * @param orderId 주문 문자열(코드)
	 * @return 주문 정보가 없는 결제 내역 응답 객체 리스트
	 */

	@Transactional(readOnly = true)
	public List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrderWithoutLogin(String orderId) {
		List<ReadBillLogWithoutOrderResponse> responses = new ArrayList<>();
		List<BillLog> billLogs = billLogRepository.findByOrder_OrderStr(orderId);

		for (BillLog newBillLog : billLogs) {
			responses.add(BillLogMapper.toDtoWithoutOrder(newBillLog));
		}

		return responses;
	}

	/**
	 * 비회원의 PaymentKey를 조회합니다.
	 *
	 * @param orderId 주문 문자열(코드)
	 * @param orderEmail 주문 시 입력한 이메일
	 * @return 비회원 결제 내역의 PaymentKey 문자열
	 */

	@Transactional(readOnly = true)
	public String getPaymentKeyWithoutLogin(String orderId, String orderEmail) {
		return billLogRepository.findByOrder_OrderStrAndOrder_OrderEmail(orderId, orderEmail)
			.getFirst()
			.getPaymentKey();
	}

	/**
	 * 회원의 PaymentKey를 조회합니다.
	 *
	 * @param orderId 주문 문자열(코드)
	 * @param userId 고객 번호
	 * @return 회원 결제 내역의 PaymentKey 문자열
	 */

	@Transactional(readOnly = true)
	public String getPaymentKey(String orderId, long userId) {
		return billLogRepository.findByOrder_OrderStrAndOrder_User_Id(orderId, userId).getFirst().getPaymentKey();
	}

	/**
	 * 다른 지불 수단(쿠폰, 포인트)에 대한 결제 취소 내역을 생성합니다.
	 *
	 * @param createCancelBillLogRequest 결제 취소 내역 생성 요청 객체
	 * @param request 로그인 아이디를 가져올 HttpServletRequest 객체
	 */

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

	/**
	 * 다른 지불 수단(쿠폰, 포인트)에 대한 환불 내역을 생성합니다.
	 *
	 * @param createCancelBillLogRequest 결제 취소 내역 생성 요청 객체
	 * @param request 로그인 아이디를 가져올 HttpServletRequest 객체
	 */

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

	/**
	 * 토스 결제 승인 실패 시 결제 내역을 롤백합니다. (포인트 결제, 쿠폰 결제, 포인트 적립 내역들 취소)
	 *
	 * @param paymentKey paymentKey
	 */

	@Transactional
	public void rollbackBillLog(String paymentKey) {
		List<BillLog> billLogs = billLogRepository.findAllByPaymentKey(paymentKey);
		for (BillLog billLog : billLogs) {
			billLogRepository.save(BillLog.builder().payment(billLog.getPayment()).price(billLog.getPrice())
				.payAt(LocalDateTime.now()).status(BillStatus.CANCELED).paymentKey(billLog.getPaymentKey()).order(billLog.getOrder()).cancelReason(PAYMENT_ERROR).build());
		}

		User user = billLogs.getFirst().getOrder().getUser();
		PointLog pointLog = pointLogRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId());
		pointService.createPointLogWithDelta(user, CANCEL_POINT_FOR_PAYMENT_ERROR_INQUIRY, -pointLog.getDelta());
	}
}
