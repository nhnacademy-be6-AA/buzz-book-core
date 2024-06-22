package store.buzzbook.core.controller.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.UpdateOrderRequest;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.order.OrderService;

@Tag(name = "Orders API", description = "주문 관련 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;
	private final UserRepository userRepository;

	@Operation(summary = "주문 리스트 조회", description = "주문 리스트 조회")
	@GetMapping("/{login-id}")
	public ResponseEntity<Page<ReadOrderResponse>> getOrders(@PathVariable("login-id") String loginId, @RequestParam("is-admin") boolean isAdmin, Pageable pageable) {
		Page<ReadOrderResponse> readOrderResponses = null;
		if (isAdmin) {
			readOrderResponses = orderService.readOrders(pageable);
		} else {
			long userId = userRepository.findByLoginId(loginId).orElseThrow(() -> new IllegalArgumentException("user not found")).getId();
			readOrderResponses = orderService.readMyOrders(userId, pageable);
		}
		return ResponseEntity.ok(readOrderResponses);
	}

	@Operation(summary = "주문 등록", description = "주문하기")
	@PostMapping
	public ResponseEntity<ReadOrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
		return ResponseEntity.ok(orderService.createOrder(createOrderRequest));
	}

	@Operation(summary = "주문 상태 수정", description = "주문 상태 변경")
	@PutMapping
	public ResponseEntity<ReadOrderResponse> updateOrder(@RequestBody UpdateOrderRequest updateOrderRequest) {
		return ResponseEntity.ok(orderService.updateOrder(updateOrderRequest));
	}

	//////////
	@Operation(summary = "주문 단건 조회", description = "주문 단건 조회")
	@GetMapping("/{id}")
	public ResponseEntity<ReadOrderResponse> getOrder(@PathVariable Long id, @RequestParam("userId") long userId) {
		return null;
	}

	@Operation(summary = "주문 상세 조회", description = "주문 상세 조회")
	@GetMapping("/{id}/detail")
	public ResponseEntity<List<ReadOrderDetailResponse>> getOrderDetails(@PathVariable Long id) {
		return null;
	}

	@Operation(summary = "주문 상태 조회", description = "주문 상태 조회")
	@GetMapping("status/{id}")
	public ResponseEntity<OrderStatus> getOrderStatus(@PathVariable int id) {
		return null;
	}

	@Operation(summary = "주문 상태 모두 조회", description = "주문 상태 모두 조회")
	@GetMapping("status")
	public ResponseEntity<List<OrderStatus>> getAllOrderStatus() {
		return null;
	}

	@Operation(summary = "주문 상태 등록", description = "주문 상태 등록")
	@PostMapping("status")
	public ResponseEntity<OrderStatus> createOrderStatus(@RequestBody OrderStatus orderStatus) {
		return null;
	}

	@Operation(summary = "주문 상태 수정", description = "주문 상태 수정")
	@PutMapping("status")
	public ResponseEntity<OrderStatus> updateOrderStatus(@RequestBody OrderStatus orderStatus) {
		return null;
	}

	@Operation(summary = "주문 상태 삭제", description = "주문 상태 삭제")
	@DeleteMapping("status/{id}")
	public ResponseEntity<String> deleteOrderStatus(@PathVariable int id) {
		return null;
	}

	@Operation(summary = "운임비 정책 조회", description = "운임비 정책 조회")
	@GetMapping("delivery-policy/{id}")
	public ResponseEntity<DeliveryPolicy> getDeliveryPolicy(@PathVariable int id) {
		return null;
	}

	@Operation(summary = "운임비 정책 모두 조회", description = "운임비 정책 모두 조회")
	@GetMapping("delivery-policy")
	public ResponseEntity<List<DeliveryPolicy>> getAllDeliveryPolicy() {
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
	@DeleteMapping("delivery-policy/{id}")
	public ResponseEntity<String> deleteDeliveryPolicy(@PathVariable int id) {
		return null;
	}

	@Operation(summary = "포장 조회", description = "포장 조회")
	@GetMapping("wrapping/{id}")
	public ResponseEntity<Wrapping> getWrapping(@PathVariable int id) {
		return null;
	}

	@Operation(summary = "포장 모두 조회", description = "포장 모두 조회")
	@GetMapping("wrapping")
	public ResponseEntity<List<Wrapping>> getAllWrappings() {
		return null;
	}

	@Operation(summary = "포장 등록", description = "포장 등록")
	@PostMapping("wrapping")
	public ResponseEntity<Wrapping> createWrapping(@RequestBody Wrapping wrapping) {
		return null;
	}

	@Operation(summary = "포장 수정", description = "포장 수정")
	@PutMapping("wrapping")
	public ResponseEntity<Wrapping> updateWrapping(@RequestBody Wrapping wrapping) {
		return null;
	}

	@Operation(summary = "포장 삭제", description = "포장 삭제")
	@DeleteMapping("wrapping/{id}")
	public ResponseEntity<String> deleteWrapping(@PathVariable int id) {
		return null;
	}
}
