package store.buzzbook.core.controller.order;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.dto.order.CreateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.CreateOrderStatusRequest;
import store.buzzbook.core.dto.order.CreateWrappingRequest;
import store.buzzbook.core.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderStatusResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.dto.order.UpdateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.UpdateOrderRequest;
import store.buzzbook.core.dto.order.UpdateOrderStatusRequest;
import store.buzzbook.core.dto.order.UpdateWrappingRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.service.order.OrderService;
import store.buzzbook.core.service.user.UserService;

@CrossOrigin(origins = "*")
@Tag(name = "Orders API", description = "주문 관련 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
	private static final String SUCCESS = "Deleted";

	private final OrderService orderService;
	private final UserService userService;

	@Operation(summary = "주문 리스트 조회", description = "주문 리스트 조회")
	@PostMapping("/list")
	public ResponseEntity<?> getOrders(@RequestBody ReadOrderRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			data = orderService.readOrders(request);
		} else {
			data = orderService.readMyOrders(request);
		}
		return ResponseEntity.ok(data);
	}

	@Operation(summary = "주문 등록", description = "주문하기")
	@PostMapping("/register")
	public ResponseEntity<ReadOrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
		ReadOrderResponse response = orderService.createOrder(createOrderRequest);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "주문 상태 수정", description = "주문 상태 변경")
	@PutMapping
	public ResponseEntity<ReadOrderResponse> updateOrder(@RequestBody UpdateOrderRequest updateOrderRequest) {
		if (updateOrderRequest.getUser().isAdmin()) {
			return ResponseEntity.ok(orderService.updateOrderWithAdmin(updateOrderRequest));
		}
		return ResponseEntity.ok(orderService.updateOrder(updateOrderRequest));
	}

	@Operation(summary = "주문 단건 조회", description = "주문 단건 조회")
	@GetMapping("/{id}")
	public ResponseEntity<ReadOrderResponse> getOrder(@PathVariable long id, @RequestParam("login-id") String loginId) {
		return ResponseEntity.ok(orderService.readOrder(id, loginId));
	}

	@Operation(summary = "주문 상세 조회", description = "주문 상세 조회")
	@GetMapping("/{id}/details")
	public ResponseEntity<List<ReadOrderDetailResponse>> getOrderDetails(@PathVariable long id) {
		return ResponseEntity.ok(orderService.readOrderDetails(id));
	}

	@Operation(summary = "주문 상태 이름으로 조회", description = "주문 상태 조회")
	@GetMapping("/status/{name}")
	public ResponseEntity<ReadOrderStatusResponse> getOrderStatusByName(@PathVariable String name) {
		return ResponseEntity.ok(orderService.readOrderStatusByName(name));
	}

	@Operation(summary = "주문 상태 아이디로 조회", description = "주문 상태 조회")
	@GetMapping("/status/{id}")
	public ResponseEntity<ReadOrderStatusResponse> getOrderStatusById(@PathVariable int id) {
		return ResponseEntity.ok(orderService.readOrderStatusById(id));
	}

	@Operation(summary = "주문 상태 모두 조회", description = "주문 상태 모두 조회")
	@GetMapping("/status")
	public ResponseEntity<List<ReadOrderStatusResponse>> getAllOrderStatus() {
		return ResponseEntity.ok(orderService.readAllOrderStatus());
	}

	@Operation(summary = "주문 상태 등록", description = "주문 상태 등록")
	@PostMapping("/status")
	public ResponseEntity<ReadOrderStatusResponse> createOrderStatus(@RequestBody CreateOrderStatusRequest request) {
		return ResponseEntity.ok(orderService.createOrderStatus(request));
	}

	@Operation(summary = "주문 상태 수정", description = "주문 상태 수정")
	@PutMapping("/status")
	public ResponseEntity<ReadOrderStatusResponse> updateOrderStatus(@RequestBody UpdateOrderStatusRequest request) {
		return ResponseEntity.ok(orderService.updateOrderStatus(request));
	}

	@Operation(summary = "주문 상태 삭제", description = "주문 상태 삭제")
	@DeleteMapping("/status/{id}")
	public ResponseEntity<String> deleteOrderStatus(@PathVariable int id) {
		orderService.deleteOrderStatus(id);
		return ResponseEntity.ok(SUCCESS);
	}

	@Operation(summary = "운임비 정책 조회", description = "운임비 정책 조회")
	@GetMapping("/delivery-policy/{id}")
	public ResponseEntity<ReadDeliveryPolicyResponse> getDeliveryPolicy(@PathVariable int id) {
		return ResponseEntity.ok(orderService.readDeliveryPolicyById(id));
	}

	@Operation(summary = "운임비 정책 모두 조회", description = "운임비 정책 모두 조회")
	@GetMapping("/delivery-policy")
	public ResponseEntity<List<ReadDeliveryPolicyResponse>> getAllDeliveryPolicy() {
		return ResponseEntity.ok(orderService.readAllDeliveryPolicy());
	}

	@Operation(summary = "운임비 정책 등록", description = "운임비 정책 등록")
	@PostMapping("/delivery-policy")
	public ResponseEntity<ReadDeliveryPolicyResponse> createDeliveryPolicy(@RequestBody CreateDeliveryPolicyRequest request) {
		return ResponseEntity.ok(orderService.createDeliveryPolicy(request));
	}

	@Operation(summary = "운임비 정책 수정", description = "운임비 정책 수정")
	@PutMapping("/delivery-policy")
	public ResponseEntity<ReadDeliveryPolicyResponse> updateDeliveryPolicy(@RequestBody UpdateDeliveryPolicyRequest request) {
		return ResponseEntity.ok(orderService.updateDeliveryPolicy(request));
	}

	@Operation(summary = "운임비 정책 삭제", description = "운임비 정책 삭제")
	@DeleteMapping("/delivery-policy/{id}")
	public ResponseEntity<String> deleteDeliveryPolicy(@PathVariable int id) {
		orderService.deleteDeliveryPolicy(id);
		return ResponseEntity.ok(SUCCESS);
	}

	@Operation(summary = "포장 조회", description = "포장 조회")
	@GetMapping("/wrapping/{id}")
	public ResponseEntity<ReadWrappingResponse> getWrapping(@PathVariable int id) {
		return ResponseEntity.ok(orderService.readWrappingById(id));
	}

	@Operation(summary = "포장 모두 조회", description = "포장 모두 조회")
	@GetMapping("/wrapping")
	public ResponseEntity<List<ReadWrappingResponse>> getAllWrappings() {
		return ResponseEntity.ok(orderService.readAllWrapping());
	}

	@Operation(summary = "포장 등록", description = "포장 등록")
	@PostMapping("/wrapping")
	public ResponseEntity<ReadWrappingResponse> createWrapping(@RequestBody CreateWrappingRequest request) {
		return ResponseEntity.ok(orderService.createWrapping(request));
	}

	@Operation(summary = "포장 수정", description = "포장 수정")
	@PutMapping("/wrapping")
	public ResponseEntity<ReadWrappingResponse> updateWrapping(@RequestBody UpdateWrappingRequest request) {
		return ResponseEntity.ok(orderService.updateWrapping(request));
	}

	@Operation(summary = "포장 삭제", description = "포장 삭제")
	@DeleteMapping("/wrapping/{id}")
	public ResponseEntity<String> deleteWrapping(@PathVariable int id) {
		orderService.deleteWrapping(id);
		return ResponseEntity.ok(SUCCESS);
	}
}
