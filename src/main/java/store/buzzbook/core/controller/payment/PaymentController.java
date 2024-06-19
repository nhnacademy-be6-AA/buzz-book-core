package store.buzzbook.core.controller.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import store.buzzbook.core.dto.payment.BillLogResponse;
import store.buzzbook.core.dto.payment.CreateBillLogRequest;
import store.buzzbook.core.dto.payment.CreatePaymentLogRequest;
import store.buzzbook.core.dto.payment.PaymentLogResponse;

@Tag(name = "Payments API", description = "결제 관련 API")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	@Operation(summary = "결제 내역 조회", description = "결제 내역 조회")
	@GetMapping("bill-log")
	public ResponseEntity<BillLogResponse> getBillLog() {
		return null;
	}

	@Operation(summary = "결제 내역 조회", description = "결제 내역 조회")
	@PostMapping("bill-log")
	public ResponseEntity<BillLogResponse> createBillLog(@RequestBody CreateBillLogRequest request) {
		return null;
	}

	@Operation(summary = "결제 수단 이력 조회", description = "결제 수단 이력 조회")
	@GetMapping("payament-log")
	public ResponseEntity<PaymentLogResponse> getPaymentLog() {
		return null;
	}

	@Operation(summary = "결제 수단 이력 조회", description = "결제 수단 이력 조회")
	@PostMapping("payament-log")
	public ResponseEntity<PaymentLogResponse> createPaymentLog(@RequestBody CreatePaymentLogRequest request) {
		return null;
	}
}
