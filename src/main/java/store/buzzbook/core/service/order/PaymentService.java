package store.buzzbook.core.service.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.order.DuplicateBillLogException;
import store.buzzbook.core.dto.order.ReadOrderWithBillLogsResponse;
import store.buzzbook.core.dto.payment.CreateCancelBillLogRequest;
import store.buzzbook.core.dto.payment.PayInfo;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.dto.payment.ReadBillLogsRequest;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.payment.BillLog;
import store.buzzbook.core.mapper.payment.BillLogMapper;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.order.WrappingRepository;
import store.buzzbook.core.repository.payment.BillLogRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.UserService;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.common.exception.user.UserNotFoundException;

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
	private final UserOrderCancelService userOrderCancelService;
	private final NonUserOrderProcessService nonUserOrderProcessService;
	private final NonUserOrderCancelService nonUserOrderCancelService;

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
	private final OrderFactory orderFactory;
	private final UserOrderProcessService userOrderProcessService;

	/**
	 *  결제 내역을 생성합니다.
	 *
	 * @param paymentInfo 토스 api에서 반환하는 payment 객체
	 * @param request 로그인 아이디를 가져올 HttpServletRequest 객체
	 */

	@Transactional(rollbackFor = Exception.class)
	public void order(PayInfo paymentInfo, HttpServletRequest request) {

		// 중복 체크
		if (billLogRepository.existsByPaymentAndPaymentKey(paymentInfo.getPayType().name(), paymentInfo.getPaymentKey())) {
			throw new DuplicateBillLogException();
		}

		Order order = orderRepository.findByOrderStr(paymentInfo.getOrderId());

		if (order.getUser() != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set(AuthService.TOKEN_HEADER, request.getHeader(AuthService.TOKEN_HEADER));
			headers.set(AuthService.REFRESH_HEADER, request.getHeader(AuthService.REFRESH_HEADER));

			orderFactory.setOrderStrategy(userOrderProcessService, order.getId(), paymentInfo, headers);
			orderFactory.process();
		} else {
			orderFactory.setOrderStrategy(nonUserOrderProcessService, order.getId(), null, null);
			orderFactory.nonUserProcess();
		}
	}

	/**
	 * 주문 번호로 조회한 주문 상세 리스트들의 상태를 수정하고 DTO 리스트로 반환합니다.
	 *
	 * @param order 주문 엔터티
	 * @param orderStatus 주문 상태 이름
	 * @return 주문 상세 응답 객체 리스트
	 */

	// private List<ReadOrderDetailResponse> getReadOrderDetailResponses(Order order, String orderStatus) {
	// 	List<ReadOrderDetailResponse> readOrderDetailResponses = new ArrayList<>();
	// 	List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
	// 	for (OrderDetail orderDetail : orderDetails) {
	// 		orderDetail.setOrderStatus(orderStatusRepository.findByName(orderStatus));
	// 		Product product = productRepository.findById(orderDetail.getProduct().getId())
	// 			.orElseThrow(ProductNotFoundException::new);
	// 		Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId())
	// 			.orElseThrow(WrappingNotFoundException::new);
	// 		ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);
	// 		readOrderDetailResponses.add(
	// 			OrderDetailMapper.toDto(orderDetail, ProductResponse.convertToProductResponse(product),
	// 				readWrappingResponse));
	// 	}
	// 	return readOrderDetailResponses;
	// }

	/**
	 * 주문 취소 내역을 생성합니다.
	 *
	 * @param paymentInfo 토스 api에서 반환하는 payment 객체
	 * @param request 로그인 아이디를 가져올 HttpServletRequest 객체
	 */

	@Transactional(rollbackFor = Exception.class)
	public void cancel(PayInfo paymentInfo, HttpServletRequest request) {

		Order order = orderRepository.findByOrderStr(paymentInfo.getOrderId());

		if (order.getUser() != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set(AuthService.TOKEN_HEADER, request.getHeader(AuthService.TOKEN_HEADER));
			headers.set(AuthService.REFRESH_HEADER, request.getHeader(AuthService.REFRESH_HEADER));

			orderFactory.setOrderStrategy(userOrderCancelService, order.getId(), paymentInfo, headers);
			orderFactory.process();
		} else {
			orderFactory.setOrderStrategy(nonUserOrderCancelService, order.getId(), null, null);
			orderFactory.nonUserProcess();
		}
	}

	/**
	 * 주문 환불 내역을 생성합니다.
	 *
	 * @param createCancelBillLogRequest 결제 취소 내역 생성 요청 객체
	 * @param request 로그인 아이디를 가져올 HttpServletRequest 객체
	 */

	@Transactional(rollbackFor = Exception.class)
	public void refund(CreateCancelBillLogRequest createCancelBillLogRequest, HttpServletRequest request) {

		Order order = orderRepository.findByOrderStr(createCancelBillLogRequest.getOrderId());

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set(AuthService.TOKEN_HEADER, request.getHeader(AuthService.TOKEN_HEADER));
		headers.set(AuthService.REFRESH_HEADER, request.getHeader(AuthService.REFRESH_HEADER));

		orderFactory.setOrderStrategy(userOrderCancelService, order.getId(), createCancelBillLogRequest.getPayInfo(), headers);
		orderFactory.process();
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

	@Transactional(readOnly = true)
	public Long getOrderIdByPaymentKey(String paymentKey) {
		return billLogRepository.findOrderIdByPaymentKey(paymentKey);
	}
}
