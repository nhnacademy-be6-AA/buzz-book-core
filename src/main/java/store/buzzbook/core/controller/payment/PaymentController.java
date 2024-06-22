package store.buzzbook.core.controller.payment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.payment.BillLogResponse;
import store.buzzbook.core.dto.payment.CreatePaymentLogRequest;
import store.buzzbook.core.dto.payment.PaymentLogResponse;
import store.buzzbook.core.dto.payment.PaymentResponse;
import store.buzzbook.core.service.payment.PaymentService;

@Tag(name = "Payments API", description = "결제 관련 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;

	@Operation(summary = "결제 내역 단건 조회", description = "결제 내역 단건 조회")
	@GetMapping("/bill-log/{order-id}")
	public ResponseEntity<BillLogResponse> getBillLog(@PathVariable("order-id") long orderId) {
		return ResponseEntity.ok(paymentService.readBillLog(orderId));
	}

	@Operation(summary = "결제 내역 모두 조회", description = "결제 내역 모두 조회")
	@GetMapping("/bill-logs")
	public ResponseEntity<Page<BillLogResponse>> getBillLogs(Pageable pageable) {
		return ResponseEntity.ok(paymentService.readBillLogs(pageable));
	}

	@Operation(summary = "결제 내역 추가", description = "결제 내역 추가")
	@PostMapping("/bill-log")
	public ResponseEntity<BillLogResponse> createBillLog(@RequestBody PaymentResponse createBillLogRequest) {
		return ResponseEntity.ok(paymentService.createBillLog(createBillLogRequest));
	}

	@Operation(summary = "결제 수단 이력 조회", description = "결제 수단 이력 조회")
	@GetMapping("/payament-log")
	public ResponseEntity<PaymentLogResponse> getPaymentLog() {
		return null;
	}

	@Operation(summary = "결제 수단 이력 추가", description = "결제 수단 이력 추가")
	@PostMapping("/payament-log")
	public ResponseEntity<PaymentLogResponse> createPaymentLog(@RequestBody CreatePaymentLogRequest request) {
		return null;
	}
}
