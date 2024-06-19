package store.buzzbook.core.controller.order;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import store.buzzbook.core.dto.order.OrderDetailResponse;
import store.buzzbook.core.dto.order.OrderResponse;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.order.Wrapping;

@Tag(name = "Orders API", description = "주문 관련 API")
@RestController
@RequestMapping("/api/orders")
public class OrderController {
	@Operation(summary = "주문 조회", description = "주문 조회")
	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
		return null;
	}

	@Operation(summary = "주문 상세 조회", description = "주문 상세 조회")
	@GetMapping("/{orderId}")
	public ResponseEntity<List<OrderDetailResponse>> getOrderDetails(@PathVariable("orderId") Long orderId) {
		return null;
	}

	@Operation(summary = "주문 상태 조회", description = "주문 상태 조회")
	@GetMapping("/{id}")
	public ResponseEntity<OrderStatus> getOrderStatus(@PathVariable int id) {
		return null;
	}

	@Operation(summary = "주문 상태 조회", description = "주문 상태 조회")
	@PostMapping("status")
	public ResponseEntity<OrderStatus> createOrderStatus(@RequestBody OrderStatus orderStatus) {
		return null;
	}

	@Operation(summary = "주문 상태 조회", description = "주문 상태 조회")
	@PutMapping("status")
	public ResponseEntity<OrderStatus> updateOrderStatus(@RequestBody OrderStatus orderStatus) {
		return null;
	}

	@Operation(summary = "주문 상태 조회", description = "주문 상태 조회")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteOrderStatus(@PathVariable int id) {
		return null;
	}

	@Operation(summary = "운임비 정책 조회", description = "운임비 정책 조회")
	@GetMapping("/{id}")
	public ResponseEntity<DeliveryPolicy> getDeliveryPolicy(@PathVariable int id) {
		return null;
	}

	@Operation(summary = "운임비 정책 등록", description = "운임비 정책 등록")
	@PostMapping("delivery-policy")
	public ResponseEntity<DeliveryPolicy> createDeliveryPolicy(@RequestBody DeliveryPolicy deliveryPolicy) {
		return null;
	}

	@Operation(summary = "운임비 정책 수정", description = "운임비 정책 수정")
	@PutMapping("delivery-policy")
	public ResponseEntity<DeliveryPolicy> updateDeliveryPolicy(@RequestBody DeliveryPolicy deliveryPolicy) {
		return null;
	}

	@Operation(summary = "운임비 정책 삭제", description = "운임비 정책 삭제")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteDeliveryPolicy(@PathVariable int id) {
		return null;
	}

	@Operation(summary = "포장 조회", description = "포장 조회")
	@GetMapping("/{id}")
	public ResponseEntity<Wrapping> getWrapping(@PathVariable int id) {
		return null;
	}

	@Operation(summary = "포장 등록", description = "포장 등록")
	@PostMapping("wrapping")
	public ResponseEntity<Wrapping> createWrapping(@RequestBody Wrapping wrapping) {
		return null;
	}

	@Operation(summary = "포장 수정", description = "포장 수정")
	@PostMapping("wrapping")
	public ResponseEntity<Wrapping> updateWrapping(@RequestBody Wrapping wrapping) {
		return null;
	}

	@Operation(summary = "포장 삭제", description = "포장 삭제")
	@PostMapping("/{id}")
	public ResponseEntity<String> deleteWrapping(@PathVariable int id) {
		return null;
	}
}
