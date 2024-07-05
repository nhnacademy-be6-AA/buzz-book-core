package store.buzzbook.core.controller.order;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.annotation.JwtOrderAdminValidate;
import store.buzzbook.core.common.annotation.JwtOrderValidate;
import store.buzzbook.core.common.annotation.JwtValidate;
import store.buzzbook.core.dto.order.CreateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.CreateOrderStatusRequest;
import store.buzzbook.core.dto.order.CreateWrappingRequest;
import store.buzzbook.core.dto.order.DeleteDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.DeleteOrderStatusRequest;
import store.buzzbook.core.dto.order.DeleteWrappingRequest;
import store.buzzbook.core.dto.order.ReadDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderWithoutLoginRequest;
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
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.order.OrderService;
import store.buzzbook.core.service.user.UserService;

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

	@JwtOrderValidate
	@Operation(summary = "주문 리스트 조회", description = "주문 리스트 조회")
	@PostMapping("/list")
	public ResponseEntity<?> getOrders(@RequestBody ReadOrdersRequest readOrdersRequest, HttpServletRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			data = orderService.readOrders(readOrdersRequest);
		} else {
			data = orderService.readMyOrders(readOrdersRequest, userInfo.loginId());
		}
		return ResponseEntity.ok(data);
	}

	// httpservletrequest
	@Operation(summary = "주문 등록", description = "주문하기")
	@PostMapping("/register")
	public ResponseEntity<ReadOrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
		ReadOrderResponse response = orderService.createOrder(createOrderRequest);
		return ResponseEntity.ok(response);
	}

	@JwtValidate
	@Operation(summary = "주문 상태 수정", description = "주문 상태 변경")
	@PutMapping
	public ResponseEntity<ReadOrderResponse> updateOrder(@RequestBody UpdateOrderRequest updateOrderRequest,
		HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			return ResponseEntity.ok(orderService.updateOrderWithAdmin(updateOrderRequest, userInfo.loginId()));
		}
		return ResponseEntity.ok(orderService.updateOrder(updateOrderRequest, userInfo.loginId()));
	}

	@JwtValidate
	@Operation(summary = "주문 상세 상태 수정", description = "주문 상세 상태 변경")
	@PutMapping("/detail")
	public ResponseEntity<ReadOrderDetailResponse> updateOrderDetail(
		@RequestBody UpdateOrderDetailRequest updateOrderDetailRequest, HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			return ResponseEntity.ok(orderService.updateOrderDetailWithAdmin(updateOrderDetailRequest));
		}
		return ResponseEntity.ok(orderService.updateOrderDetail(updateOrderDetailRequest, userInfo.loginId()));
	}

	@JwtValidate
	@Operation(summary = "주문 조회", description = "주문 조회")
	@PostMapping("/id")
	public ResponseEntity<ReadOrderResponse> getOrder(@RequestBody ReadOrderRequest readOrderRequest) {
		return ResponseEntity.ok(orderService.readOrder(readOrderRequest));
	}

	@Operation(summary = "비회원 주문 조회", description = "비회원 주문 조회")
	@PostMapping("non-member")
	public ResponseEntity<ReadOrderResponse> getOrderWithoutLogin(
		@RequestBody ReadOrderWithoutLoginRequest readOrderWithoutLoginRequest) {
		return ResponseEntity.ok(orderService.readOrderWithoutLogin(readOrderWithoutLoginRequest));
	}

	@JwtValidate
	@Operation(summary = "주문 상태 이름으로 조회", description = "주문 상태 조회")
	@PostMapping("/status/name")
	public ResponseEntity<ReadOrderStatusResponse> getOrderStatusByName(
		@RequestBody ReadOrderStatusByNameRequest readOrderStatusByNameRequest) {
		return ResponseEntity.ok(orderService.readOrderStatusByName(readOrderStatusByNameRequest.getName()));
	}

	@JwtValidate
	@Operation(summary = "주문 상태 아이디로 조회", description = "주문 상태 조회")
	@PostMapping("/status/id")
	public ResponseEntity<ReadOrderStatusResponse> getOrderStatusById(
		@RequestBody ReadOrderStatusByIdRequest readOrderStatusByIdRequest) {
		return ResponseEntity.ok(orderService.readOrderStatusById(readOrderStatusByIdRequest.getStatusId()));
	}

	@JwtValidate
	@Operation(summary = "주문 상태 모두 조회", description = "주문 상태 모두 조회")
	@GetMapping("/status/all")
	public ResponseEntity<List<ReadOrderStatusResponse>> getAllOrderStatus() {
		return ResponseEntity.ok(orderService.readAllOrderStatus());
	}

	@JwtOrderAdminValidate
	@Operation(summary = "주문 상태 등록", description = "주문 상태 등록")
	@PostMapping("/status")
	public ResponseEntity<?> createOrderStatus(@RequestBody CreateOrderStatusRequest createOrderStatusRequest,
		HttpServletRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.createOrderStatus(createOrderStatusRequest)));
		}
		return ResponseEntity.ok(data);
	}

	@JwtOrderAdminValidate
	@Operation(summary = "주문 상태 수정", description = "주문 상태 수정")
	@PutMapping("/status")
	public ResponseEntity<?> updateOrderStatus(@RequestBody UpdateOrderStatusRequest updateOrderStatusRequest,
		HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.updateOrderStatus(updateOrderStatusRequest)));
		}
		return ResponseEntity.ok(data);
	}

	@JwtValidate
	@Operation(summary = "주문 상태 삭제", description = "주문 상태 삭제")
	@DeleteMapping("/status")
	public ResponseEntity<String> deleteOrderStatus(@RequestBody DeleteOrderStatusRequest deleteOrderStatusRequest,
		HttpServletRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			orderService.deleteOrderStatus(deleteOrderStatusRequest.getId());
			return ResponseEntity.ok(SUCCESS);
		}

		return ResponseEntity.ok(FAILURE);
	}

	@JwtValidate
	@Operation(summary = "운임비 정책 조회", description = "운임비 정책 조회")
	@PostMapping("/delivery-policy/id")
	public ResponseEntity<ReadDeliveryPolicyResponse> getDeliveryPolicy(
		@RequestBody ReadDeliveryPolicyRequest readDeliveryPolicyRequest) {
		return ResponseEntity.ok(orderService.readDeliveryPolicyById(readDeliveryPolicyRequest.getId()));
	}

	@JwtValidate
	@Operation(summary = "운임비 정책 모두 조회", description = "운임비 정책 모두 조회")
	@GetMapping("/delivery-policy/all")
	public ResponseEntity<List<ReadDeliveryPolicyResponse>> getAllDeliveryPolicy() {
		return ResponseEntity.ok(orderService.readAllDeliveryPolicy());
	}

	@JwtOrderAdminValidate
	@Operation(summary = "운임비 정책 등록", description = "운임비 정책 등록")
	@PostMapping("/delivery-policy")
	public ResponseEntity<?> createDeliveryPolicy(@RequestBody CreateDeliveryPolicyRequest createDeliveryPolicyRequest,
		HttpServletRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.createDeliveryPolicy(createDeliveryPolicyRequest)));
		}
		return ResponseEntity.ok(data);
	}

	@JwtOrderAdminValidate
	@Operation(summary = "운임비 정책 수정", description = "운임비 정책 수정")
	@PutMapping("/delivery-policy")
	public ResponseEntity<?> updateDeliveryPolicy(@RequestBody UpdateDeliveryPolicyRequest updateDeliveryPolicyRequest,
		HttpServletRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.updateDeliveryPolicy(updateDeliveryPolicyRequest)));
		}
		return ResponseEntity.ok(data);
	}

	@JwtValidate
	@Operation(summary = "운임비 정책 삭제", description = "운임비 정책 삭제")
	@DeleteMapping("/delivery-policy")
	public ResponseEntity<String> deleteDeliveryPolicy(
		@RequestBody DeleteDeliveryPolicyRequest deleteDeliveryPolicyRequest, HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			orderService.deleteDeliveryPolicy(deleteDeliveryPolicyRequest.getId());
			return ResponseEntity.ok(SUCCESS);
		}

		return ResponseEntity.ok(FAILURE);
	}

	@JwtValidate
	@Operation(summary = "포장 조회", description = "포장 조회")
	@PostMapping("/wrapping/id")
	public ResponseEntity<ReadWrappingResponse> getWrapping(@RequestBody ReadWrappingRequest readWrappingRequest) {
		return ResponseEntity.ok(orderService.readWrappingById(readWrappingRequest.getId()));
	}

	@JwtValidate
	@Operation(summary = "포장 모두 조회", description = "포장 모두 조회")
	@GetMapping("/wrapping/all")
	public ResponseEntity<List<ReadWrappingResponse>> getAllWrappings() {
		return ResponseEntity.ok(orderService.readAllWrapping());
	}

	@JwtOrderAdminValidate
	@Operation(summary = "포장 등록", description = "포장 등록")
	@PostMapping("/wrapping")
	public ResponseEntity<?> createWrapping(@RequestBody CreateWrappingRequest createWrappingRequest,
		HttpServletRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.createWrapping(createWrappingRequest)));
		}
		return ResponseEntity.ok(data);
	}

	@JwtOrderAdminValidate
	@Operation(summary = "포장 수정", description = "포장 수정")
	@PutMapping("/wrapping")
	public ResponseEntity<?> updateWrapping(@RequestBody UpdateWrappingRequest updateWrappingRequest,
		HttpServletRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			data.put("responseData", ResponseEntity.ok(orderService.updateWrapping(updateWrappingRequest)));
		}
		return ResponseEntity.ok(data);
	}

	@JwtValidate
	@Operation(summary = "포장 삭제", description = "포장 삭제")
	@DeleteMapping("/wrapping")
	public ResponseEntity<String> deleteWrapping(@RequestBody DeleteWrappingRequest deleteWrappingRequest,
		HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			orderService.deleteWrapping(deleteWrappingRequest.getId());
			return ResponseEntity.ok(SUCCESS);
		}
		return ResponseEntity.ok(FAILURE);
	}
}
