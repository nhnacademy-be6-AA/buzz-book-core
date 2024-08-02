package store.buzzbook.core.controller.payment;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.annotation.JwtOrderAdminValidate;
import store.buzzbook.core.common.annotation.JwtOrderValidate;
import store.buzzbook.core.dto.payment.CreateCancelBillLogRequest;
import store.buzzbook.core.dto.payment.PayInfo;
import store.buzzbook.core.dto.payment.ReadBillLogsRequest;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.dto.payment.ReadPaymentKeyRequest;
import store.buzzbook.core.dto.payment.ReadBillLogRequest;
import store.buzzbook.core.dto.payment.ReadPaymentKeyWithOrderDetailRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.order.OrderService;
import store.buzzbook.core.service.order.PaymentService;
import store.buzzbook.core.service.user.UserService;

/**
 * 결제 관련 컨트롤러
 *
 * @author 박설
 */

@Slf4j
@Tag(name = "결제", description = "결제 관련 api")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
	private static final String CANCELED = "Canceled";

	private final PaymentService paymentService;
	private final UserService userService;
	private final OrderService orderService;

	@JwtOrderValidate
	@Operation(summary = "주문 하나에 딸린 결제 내역들 조회", description = "결제 내역 단건 조회")
	@PostMapping("/bill-logs")
	public ResponseEntity<List<ReadBillLogWithoutOrderResponse>> getBillLogs(@RequestBody ReadBillLogRequest readBillLogRequest, HttpServletRequest request) {
		if (request.getAttribute(AuthService.LOGIN_ID) == null) {
			List<ReadBillLogWithoutOrderResponse> responses = paymentService.readBillLogWithoutOrderWithoutLogin(readBillLogRequest.getOrderId());
			return ResponseEntity.ok(responses);
		}

		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));

		if (userInfo.isAdmin()) {
			return ResponseEntity.ok(paymentService.readBillLogWithoutOrderWithAdmin(readBillLogRequest.getOrderId()));
		}
		List<ReadBillLogWithoutOrderResponse> responses = paymentService.readBillLogWithoutOrder(userInfo.id(), readBillLogRequest.getOrderId());
		return ResponseEntity.ok(responses);
	}

	@JwtOrderAdminValidate
	@Operation(summary = "관리자의 결제 내역 모두 조회", description = "결제 내역 모두 조회 - 관리자")
	@PostMapping("/admin/bill-logs")
	public ResponseEntity<Map<String, Object>> getAllBillLogs(@RequestBody ReadBillLogsRequest readBillLogsRequest, HttpServletRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			data = paymentService.readBillLogs(readBillLogsRequest);
		}

		return ResponseEntity.ok(data);
	}

	@Operation(summary = "주문 결제", description = "주문 결제")
	@PostMapping("/order")
	public ResponseEntity<String> order(@RequestBody PayInfo paymentInfo, HttpServletRequest request) {
		paymentService.order(paymentInfo, request);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "주문 취소", description = "주문 취소")
	@PostMapping("/cancel")
	public ResponseEntity<String> cancel(@RequestBody PayInfo paymentInfo, HttpServletRequest request) {
		paymentService.cancel(paymentInfo, request);
		return ResponseEntity.ok().build();
	}

	@JwtOrderValidate
	@Operation(summary = "주문 환불", description = "주문 환불")
	@PostMapping("/refund")
	public ResponseEntity<String> refund(@RequestBody CreateCancelBillLogRequest createCancelBillLogRequest, HttpServletRequest request) {
		paymentService.refund(createCancelBillLogRequest, request);
		return ResponseEntity.ok(CANCELED);
	}

	@JwtOrderValidate
	@Operation(summary = "결제키 조회", description = "결제키 조회")
	@PostMapping("/payment-key")
	public ResponseEntity<String> getPaymentKey(@RequestBody ReadPaymentKeyRequest readPaymentKeyRequest, HttpServletRequest request) {
		if (request.getAttribute(AuthService.LOGIN_ID) == null) {
			String responses = paymentService.getPaymentKeyWithoutLogin(readPaymentKeyRequest.getOrderId(), readPaymentKeyRequest.getOrderEmail());
			return ResponseEntity.ok(responses);
		}
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));

		return ResponseEntity.ok(paymentService.getPaymentKey(readPaymentKeyRequest.getOrderId(), userInfo.id()));
	}

	@JwtOrderValidate
	@Operation(summary = "주문상세 아이디로 결제키 조회", description = "주문상세 아이디로 결제키 조회")
	@PostMapping("/detail/payment-key")
	public ResponseEntity<String> getPaymentKeyByOrderDetailId(@RequestBody ReadPaymentKeyWithOrderDetailRequest readPaymentKeyWithOrderDetailRequest, HttpServletRequest request) {
		if (request.getAttribute(AuthService.LOGIN_ID) == null) {
			String responses = paymentService.getPaymentKeyWithoutLogin(orderService.readOrderStr(readPaymentKeyWithOrderDetailRequest.getOrderDetailId()), readPaymentKeyWithOrderDetailRequest.getOrderEmail());
			return ResponseEntity.ok(responses);
		}
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));

		return ResponseEntity.ok(paymentService.getPaymentKey(orderService.readOrderStr(readPaymentKeyWithOrderDetailRequest.getOrderDetailId()), userInfo.id()));
	}
}
