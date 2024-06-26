package store.buzzbook.core.controller.payment;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.payment.ReadAllPaymentLogRequest;
import store.buzzbook.core.dto.payment.ReadBillLogsRequest;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.dto.payment.ReadPaymentLogRequest;
import store.buzzbook.core.dto.payment.ReadPaymentLogResponse;
import store.buzzbook.core.dto.payment.ReadBillLogRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.service.payment.PaymentService;
import store.buzzbook.core.service.user.UserService;

@CrossOrigin(origins = "*")
@Tag(name = "Payments API", description = "결제 관련 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;
	private final UserService userService;

	@Operation(summary = "주문 하나에 딸린 결제 내역들 조회", description = "결제 내역 단건 조회")
	@PostMapping("/bill-logs")
	public ResponseEntity<List<ReadBillLogWithoutOrderResponse>> getBillLogs(@RequestBody ReadBillLogRequest request) {
		long userId = userService.getUserInfoByLoginId(request.getLoginId()).id();

		return ResponseEntity.ok(paymentService.readBillLogWithoutOrder(userId, request.getOrderId()));
	}

	@Operation(summary = "관리자의 결제 내역 모두 조회", description = "결제 내역 모두 조회 - 관리자")
	@PostMapping("/admin/bill-logs")
	public ResponseEntity<?> getAllBillLogs(@RequestBody ReadBillLogsRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			data = paymentService.readBillLogs(request);
		}

		return ResponseEntity.ok(data);
	}

	@Operation(summary = "결제 내역 추가", description = "결제 내역 추가")
	@PostMapping("/bill-log")
	public ResponseEntity<ReadBillLogResponse> createBillLog(@RequestBody JSONObject createBillLogRequest) {
		return ResponseEntity.ok(paymentService.createBillLog(createBillLogRequest));
	}

	@Operation(summary = "결제 수단 이력 조회", description = "결제 수단 이력 조회")
	@GetMapping("/payment-log/id")
	public ResponseEntity<List<ReadPaymentLogResponse>> getPaymentLogs(@RequestBody ReadPaymentLogRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			return ResponseEntity.ok(paymentService.readPaymentLogs(request.getOrderStr()));
		}
		return ResponseEntity.ok(paymentService.readPaymentLogs(request));
	}

	@Operation(summary = "결제 수단 이력 모두 조회", description = "결제 수단 이력 모두 조회")
	@GetMapping("/payment-log/all")
	public ResponseEntity<List<ReadPaymentLogResponse>> getAllPaymentLogs(@RequestBody ReadAllPaymentLogRequest request) {
		return null;
	}

	@Operation(summary = "결제 수단 이력 추가", description = "결제 수단 이력 추가")
	@PostMapping("/payment-log")
	public ResponseEntity<ReadPaymentLogResponse> createPaymentLog(@RequestBody JSONObject createPaymentLogRequest) {
		// return ResponseEntity.ok(paymentService.createPaymentLog(createPaymentLogRequest));
		return null;
	}
}
