package store.buzzbook.core.controller.order;

import java.util.List;
import java.util.Map;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.annotation.JwtOrderAdminValidate;
import store.buzzbook.core.common.annotation.JwtOrderValidate;
import store.buzzbook.core.dto.order.CreateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.CreatePointLogForOrderRequest;
import store.buzzbook.core.dto.order.CreateWrappingRequest;
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
import store.buzzbook.core.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.core.dto.order.UpdateOrderRequest;
import store.buzzbook.core.dto.point.PointLogResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.order.OrderService;
import store.buzzbook.core.service.user.UserService;

/**
 * 주문 관련 컨트롤러
 *
 * @author 박설
 */

@Tag(name = "주문", description = "주문 관련 api")
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
	public ResponseEntity<Map<String, Object>> getOrders(@RequestBody ReadOrdersRequest readOrdersRequest, HttpServletRequest request) {
		Map<String, Object> data = null;
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			data = orderService.readOrders(readOrdersRequest);
		} else {
			data = orderService.readMyOrders(readOrdersRequest, userInfo.loginId());
		}
		return ResponseEntity.ok(data);
	}

	@JwtOrderValidate
	@Operation(summary = "주문 등록", description = "주문하기")
	@PostMapping("/register")
	public ResponseEntity<ReadOrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest, HttpServletRequest request) {
		String loginId = (String)request.getAttribute(AuthService.LOGIN_ID);
		if (loginId == null) {
			return ResponseEntity.ok(orderService.createOrder(createOrderRequest));
		}
		UserInfo userInfo = userService.getUserInfoByLoginId(loginId);
		createOrderRequest.setLoginId(userInfo.loginId());
		return ResponseEntity.ok(orderService.createOrder(createOrderRequest));
	}

	@JwtOrderValidate
	@Operation(summary = "주문 상태 수정", description = "주문 상태 변경")
	@PutMapping
	public ResponseEntity<ReadOrderResponse> updateOrder(@RequestBody UpdateOrderRequest updateOrderRequest,
		HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			return ResponseEntity.ok(orderService.updateOrderWithAdmin(updateOrderRequest));
		}
		return ResponseEntity.ok(orderService.updateOrder(updateOrderRequest, userInfo.loginId()));
	}

	@JwtOrderValidate
	@Operation(summary = "주문 상세 상태 수정", description = "주문 상세 상태 변경")
	@PutMapping("/detail")
	public ResponseEntity<ReadOrderDetailResponse> updateOrderDetail(
		@RequestBody UpdateOrderDetailRequest updateOrderDetailRequest, HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			return ResponseEntity.ok(orderService.updateOrderDetailWithAdmin(updateOrderDetailRequest));
		}
		// 비회원 주문취소
		return ResponseEntity.ok(orderService.updateOrderDetail(updateOrderDetailRequest, userInfo.loginId()));
	}

	@JwtOrderValidate
	@Operation(summary = "주문 조회", description = "주문 조회")
	@PostMapping("/id")
	public ResponseEntity<ReadOrderResponse> getOrder(@RequestBody ReadOrderRequest readOrderRequest, HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		ReadOrderResponse response = orderService.readOrder(readOrderRequest, userInfo.loginId());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "비회원 주문 조회", description = "비회원 주문 조회")
	@PostMapping("/non-member")
	public ResponseEntity<ReadOrderResponse> getOrderWithoutLogin(
		@RequestBody ReadOrderWithoutLoginRequest readOrderWithoutLoginRequest) {
		return ResponseEntity.ok(orderService.readOrderWithoutLogin(readOrderWithoutLoginRequest));
	}

	// @JwtOrderValidate
	// @Operation(summary = "주문 및 취소 시 포인트 변경", description = "주문 및 취소 시 포인트 변경")
	// @PostMapping("/point")
	// public ResponseEntity<PointLogResponse> updatePointLog(@RequestBody CreatePointLogForOrderRequest createPointLogForOrderRequest, HttpServletRequest request) {
	// 	UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
	// 	return ResponseEntity.ok(orderService.createPointLog(createPointLogForOrderRequest, userInfo));
	// }

	@Operation(summary = "주문 상태 이름으로 조회", description = "주문 상태 조회")
	@PostMapping("/status/name")
	public ResponseEntity<ReadOrderStatusResponse> getOrderStatusByName(
		@RequestBody ReadOrderStatusByNameRequest readOrderStatusByNameRequest) {
		return ResponseEntity.ok(orderService.readOrderStatusByName(readOrderStatusByNameRequest.getName()));
	}

	@Operation(summary = "주문 상태 아이디로 조회", description = "주문 상태 조회")
	@PostMapping("/status/id")
	public ResponseEntity<ReadOrderStatusResponse> getOrderStatusById(
		@RequestBody ReadOrderStatusByIdRequest readOrderStatusByIdRequest) {
		return ResponseEntity.ok(orderService.readOrderStatusById(readOrderStatusByIdRequest.getStatusId()));
	}

	@Operation(summary = "주문 상태 모두 조회", description = "주문 상태 모두 조회")
	@GetMapping("/status/all")
	public ResponseEntity<List<ReadOrderStatusResponse>> getAllOrderStatus() {
		return ResponseEntity.ok(orderService.readAllOrderStatus());
	}

	@Operation(summary = "운임비 정책 조회", description = "운임비 정책 조회")
	@PostMapping("/delivery-policy/id")
	public ResponseEntity<ReadDeliveryPolicyResponse> getDeliveryPolicy(
		@RequestBody ReadDeliveryPolicyRequest readDeliveryPolicyRequest) {
		return ResponseEntity.ok(orderService.readDeliveryPolicyById(readDeliveryPolicyRequest.getId()));
	}

	@Operation(summary = "운임비 정책 모두 조회", description = "운임비 정책 모두 조회")
	@GetMapping("/delivery-policy/all")
	public ResponseEntity<List<ReadDeliveryPolicyResponse>> getAllDeliveryPolicy() {
		return ResponseEntity.ok(orderService.readAllDeliveryPolicy());
	}

	@JwtOrderAdminValidate
	@Operation(summary = "운임비 정책 등록", description = "운임비 정책 등록")
	@PostMapping("/delivery-policy")
	public ResponseEntity<ReadDeliveryPolicyResponse> createDeliveryPolicy(@RequestBody CreateDeliveryPolicyRequest createDeliveryPolicyRequest,
		HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			return ResponseEntity.ok(orderService.createDeliveryPolicy(createDeliveryPolicyRequest));
		}
		throw new NotAuthorizedException("관리자 계정으로 접속해주세요.");
	}

	@JwtOrderAdminValidate
	@Operation(summary = "운임비 정책 삭제", description = "운임비 정책 삭제")
	@DeleteMapping("/delivery-policy/{id}")
	public ResponseEntity<String> deleteDeliveryPolicy(@PathVariable int id, HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			orderService.deleteDeliveryPolicy(id);
			return ResponseEntity.ok(SUCCESS);
		}
		throw new NotAuthorizedException("관리자 계정으로 접속해주세요.");
	}

	@Operation(summary = "포장 조회", description = "포장 조회")
	@PostMapping("/wrapping/id")
	public ResponseEntity<ReadWrappingResponse> getWrapping(@RequestBody ReadWrappingRequest readWrappingRequest) {
		return ResponseEntity.ok(orderService.readWrappingById(readWrappingRequest.getId()));
	}

	@Operation(summary = "포장 모두 조회", description = "포장 모두 조회")
	@GetMapping("/wrapping/all")
	public ResponseEntity<List<ReadWrappingResponse>> getAllWrappings() {
		return ResponseEntity.ok(orderService.readAllWrapping());
	}

	@JwtOrderAdminValidate
	@Operation(summary = "포장 등록", description = "포장 등록")
	@PostMapping("/wrapping")
	public ResponseEntity<ReadWrappingResponse> createWrapping(@RequestBody CreateWrappingRequest createWrappingRequest,
		HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			return ResponseEntity.ok(orderService.createWrapping(createWrappingRequest));
		}
		throw new NotAuthorizedException("관리자 계정으로 접속해주세요.");
	}

	@JwtOrderAdminValidate
	@Operation(summary = "포장 삭제", description = "포장 삭제")
	@DeleteMapping("/wrapping/{id}")
	public ResponseEntity<String> deleteWrapping(@PathVariable int id, HttpServletRequest request) {
		UserInfo userInfo = userService.getUserInfoByLoginId((String)request.getAttribute(AuthService.LOGIN_ID));
		if (userInfo.isAdmin()) {
			orderService.deleteWrapping(id);
			return ResponseEntity.ok(SUCCESS);
		}
		throw new NotAuthorizedException("관리자 계정으로 접속해주세요.");
	}
}
