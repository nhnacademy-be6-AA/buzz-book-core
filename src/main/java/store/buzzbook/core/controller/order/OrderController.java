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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.dto.order.CreateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.CreateOrderStatusRequest;
import store.buzzbook.core.dto.order.CreateWrappingRequest;
import store.buzzbook.core.dto.order.DeleteDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.DeleteOrderStatusRequest;
import store.buzzbook.core.dto.order.DeleteWrappingRequest;
import store.buzzbook.core.dto.order.ReadAllDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.ReadAllOrderStatusRequest;
import store.buzzbook.core.dto.order.ReadAllWrappingRequest;
import store.buzzbook.core.dto.order.ReadDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrdersRequest;
import store.buzzbook.core.dto.order.ReadOrderStatusByIdRequest;
import store.buzzbook.core.dto.order.ReadOrderStatusByNameRequest;
import store.buzzbook.core.dto.order.ReadOrderStatusResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.ReadWrappingRequest;
import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.dto.order.UpdateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.UpdateOrderDetailRequest;
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
	private static final String FAILURE = "Failed";

	private final OrderService orderService;
	private final UserService userService;

	@Operation(summary = "주문 리스트 조회", description = "주문 리스트 조회")
	@PostMapping("/list")
	public ResponseEntity<?> getOrders(@RequestBody ReadOrdersRequest request) {
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
	public ResponseEntity<ReadOrderResponse> updateOrder(@RequestBody UpdateOrderRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			return ResponseEntity.ok(orderService.updateOrderWithAdmin(request));
		}
		return ResponseEntity.ok(orderService.updateOrder(request));
	}

	@Operation(summary = "주문 상세 상태 수정", description = "주문 상세 상태 변경")
	@PutMapping("/detail")
	public ResponseEntity<ReadOrderDetailResponse> updateOrderDetail(@RequestBody UpdateOrderDetailRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			return ResponseEntity.ok(orderService.updateOrderDetailWithAdmin(request));
		}
		return ResponseEntity.ok(orderService.updateOrderDetail(request));
	}

	@Operation(summary = "주문 조회", description = "주문 조회")
	@PostMapping("/id")
	public ResponseEntity<ReadOrderResponse> getOrder(@RequestBody ReadOrderRequest request) {
		return ResponseEntity.ok(orderService.readOrder(request));
	}

	@Operation(summary = "주문 상태 이름으로 조회", description = "주문 상태 조회")
	@PostMapping("/status/name")
	public ResponseEntity<ReadOrderStatusResponse> getOrderStatusByName(@RequestBody ReadOrderStatusByNameRequest request) {
		return ResponseEntity.ok(orderService.readOrderStatusByName(request.getName()));
	}

	@Operation(summary = "주문 상태 아이디로 조회", description = "주문 상태 조회")
	@PostMapping("/status/id")
	public ResponseEntity<ReadOrderStatusResponse> getOrderStatusById(@RequestBody ReadOrderStatusByIdRequest request) {
		return ResponseEntity.ok(orderService.readOrderStatusById(request.getStatusId()));
	}

	@Operation(summary = "주문 상태 모두 조회", description = "주문 상태 모두 조회")
	@PostMapping("/status/all")
	public ResponseEntity<List<ReadOrderStatusResponse>> getAllOrderStatus(@RequestBody ReadAllOrderStatusRequest request) {
		return ResponseEntity.ok(orderService.readAllOrderStatus());
	}

	@Operation(summary = "주문 상태 등록", description = "주문 상태 등록")
	@PostMapping("/status")
	public ResponseEntity<?> createOrderStatus(@RequestBody CreateOrderStatusRequest request) {
		Map<String, Object> data = null;
		if (request.getUserInfo().isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.createOrderStatus(request)));
		}
		return ResponseEntity.ok(data);
	}

	@Operation(summary = "주문 상태 수정", description = "주문 상태 수정")
	@PutMapping("/status")
	public ResponseEntity<?> updateOrderStatus(@RequestBody UpdateOrderStatusRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.updateOrderStatus(request)));
		}
		return ResponseEntity.ok(data);
	}

	@Operation(summary = "주문 상태 삭제", description = "주문 상태 삭제")
	@DeleteMapping("/status")
	public ResponseEntity<String> deleteOrderStatus(@RequestBody DeleteOrderStatusRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			orderService.deleteOrderStatus(request.getId());
			return ResponseEntity.ok(SUCCESS);
		}

		return ResponseEntity.ok(FAILURE);
	}

	@Operation(summary = "운임비 정책 조회", description = "운임비 정책 조회")
	@PostMapping("/delivery-policy/id")
	public ResponseEntity<ReadDeliveryPolicyResponse> getDeliveryPolicy(@RequestBody ReadDeliveryPolicyRequest request) {
		return ResponseEntity.ok(orderService.readDeliveryPolicyById(request.getId()));
	}

	@Operation(summary = "운임비 정책 모두 조회", description = "운임비 정책 모두 조회")
	@PostMapping("/delivery-policy/all")
	public ResponseEntity<List<ReadDeliveryPolicyResponse>> getAllDeliveryPolicy(@RequestBody ReadAllDeliveryPolicyRequest request) {
		return ResponseEntity.ok(orderService.readAllDeliveryPolicy());
	}

	@Operation(summary = "운임비 정책 등록", description = "운임비 정책 등록")
	@PostMapping("/delivery-policy")
	public ResponseEntity<?> createDeliveryPolicy(@RequestBody CreateDeliveryPolicyRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.createDeliveryPolicy(request)));
		}
		return ResponseEntity.ok(data);
	}

	@Operation(summary = "운임비 정책 수정", description = "운임비 정책 수정")
	@PutMapping("/delivery-policy")
	public ResponseEntity<?> updateDeliveryPolicy(@RequestBody UpdateDeliveryPolicyRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.updateDeliveryPolicy(request)));
		}
		return ResponseEntity.ok(data);
	}

	@Operation(summary = "운임비 정책 삭제", description = "운임비 정책 삭제")
	@DeleteMapping("/delivery-policy")
	public ResponseEntity<String> deleteDeliveryPolicy(@RequestBody DeleteDeliveryPolicyRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			orderService.deleteDeliveryPolicy(request.getId());
			return ResponseEntity.ok(SUCCESS);
		}

		return ResponseEntity.ok(FAILURE);
	}

	@Operation(summary = "포장 조회", description = "포장 조회")
	@PostMapping("/wrapping/id")
	public ResponseEntity<ReadWrappingResponse> getWrapping(@RequestBody ReadWrappingRequest request) {
		return ResponseEntity.ok(orderService.readWrappingById(request.getId()));
	}

	@Operation(summary = "포장 모두 조회", description = "포장 모두 조회")
	@PostMapping("/wrapping/all")
	public ResponseEntity<List<ReadWrappingResponse>> getAllWrappings(@RequestBody ReadAllWrappingRequest request) {
		return ResponseEntity.ok(orderService.readAllWrapping());
	}

	@Operation(summary = "포장 등록", description = "포장 등록")
	@PostMapping("/wrapping")
	public ResponseEntity<?> createWrapping(@RequestBody CreateWrappingRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.createWrapping(request)));
		}
		return ResponseEntity.ok(data);
	}

	@Operation(summary = "포장 수정", description = "포장 수정")
	@PutMapping("/wrapping")
	public ResponseEntity<?> updateWrapping(@RequestBody UpdateWrappingRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.updateWrapping(request)));
		}
		return ResponseEntity.ok(data);
	}

	@Operation(summary = "포장 삭제", description = "포장 삭제")
	@DeleteMapping("/wrapping")
	public ResponseEntity<String> deleteWrapping(@RequestBody DeleteWrappingRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId(request.getLoginId());
		if (userInfo.isAdmin()) {
			orderService.deleteWrapping(request.getId());
			return ResponseEntity.ok(SUCCESS);
		}
		return ResponseEntity.ok(FAILURE);
	}
}
