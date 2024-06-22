package store.buzzbook.core.controller.payment;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.dto.payment.CreatePaymentLogRequest;
import store.buzzbook.core.dto.payment.ReadPaymentLogResponse;
import store.buzzbook.core.dto.payment.ReadPaymentResponse;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.payment.PaymentService;

@Tag(name = "Payments API", description = "결제 관련 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;
	private final UserRepository userRepository;

	@Operation(summary = "결제 내역 단건 조회", description = "결제 내역 단건 조회")
	@GetMapping("/bill-log/{order-id}")
	public ResponseEntity<ReadBillLogResponse> getBillLog(@PathVariable("order-id") long orderId, @RequestParam("login-id") String loginId) {
		long userId = userRepository.findByLoginId(loginId).orElseThrow(() -> new IllegalArgumentException("user not found")).getId();
		return ResponseEntity.ok(paymentService.readBillLog(userId, orderId));
	}

	@Operation(summary = "결제 내역 모두 조회", description = "결제 내역 모두 조회")
	@GetMapping("/bill-logs")
	public ResponseEntity<Page<ReadBillLogResponse>> getAllBillLogs(@RequestParam("login-id") String loginId, @RequestParam("is-admin") boolean isAdmin, Pageable pageable) {
		Page<ReadBillLogResponse> billLogResponses = null;
		if (isAdmin) {
			billLogResponses = paymentService.readBillLogs(pageable);
		} else {
			billLogResponses = paymentService.readMyBillLogs(loginId, pageable);
		}

		return ResponseEntity.ok(billLogResponses);
	}

	@Operation(summary = "결제 내역 추가", description = "결제 내역 추가")
	@PostMapping("/bill-log")
	public ResponseEntity<ReadBillLogResponse> createBillLog(@RequestBody ReadPaymentResponse createBillLogRequest) {
		return ResponseEntity.ok(paymentService.createBillLog(createBillLogRequest));
	}

	@Operation(summary = "결제 수단 이력 조회", description = "결제 수단 이력 조회")
	@GetMapping("/payament-log/{id}")
	public ResponseEntity<ReadPaymentLogResponse> getPaymentLog(@PathVariable("id") long id) {
		return null;
	}

	@Operation(summary = "결제 수단 이력 모두 조회", description = "결제 수단 이력 모두 조회")
	@GetMapping("/payament-log")
	public ResponseEntity<List<ReadPaymentLogResponse>> getAllPaymentLogs() {
		return null;
	}

	@Operation(summary = "결제 수단 이력 추가", description = "결제 수단 이력 추가")
	@PostMapping("/payament-log")
	public ResponseEntity<ReadPaymentLogResponse> createPaymentLog(@RequestBody CreatePaymentLogRequest request) {
		return null;
	}
}
