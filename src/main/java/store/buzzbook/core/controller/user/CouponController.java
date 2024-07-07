package store.buzzbook.core.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.annotation.JwtOrderValidate;
import store.buzzbook.core.common.annotation.JwtValidate;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.coupon.DeleteUserCouponRequest;
import store.buzzbook.core.dto.coupon.DownloadCouponRequest;
import store.buzzbook.core.dto.coupon.OrderCouponDetailResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.coupon.CouponService;

@Tag(name = "회원의 쿠폰 관련 컨트롤러", description = "유저의 쿠폰 조회, 추가 관리")
@RestController
@RequestMapping("/api/account/coupons")
@RequiredArgsConstructor
public class CouponController {

	private final CouponService couponService;

	@JwtValidate
	@PostMapping
	@Operation(summary = "쿠폰 추가", description = "유저의 쿠폰 리스트를 추가한다. user id 와 coupon id 를 넘겨야 한다.")
	public ResponseEntity<Void> downloadCoupon(@Valid @RequestBody DownloadCouponRequest request) {
		couponService.createUserCoupon(request);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/birthday")
	@Operation(summary = "유저 요청", description = "현재 월에 생일이 해당하는 유저 정보를 요청합니다.")
	public ResponseEntity<List<UserInfo>> getUsersByBirthday() {
		return ResponseEntity.ok(couponService.getUserInfoByCurrentBirthday());
	}

	@JwtOrderValidate
	@PostMapping("/order")
	@Operation(summary = "유저 주문 쿠폰 요청", description = "유저의 주문 목록에서 적용 가능한 쿠폰을 요청합니다.")
	public ResponseEntity<List<OrderCouponDetailResponse>> getOrderCoupons(
		@RequestBody List<CartDetailResponse> responses,
		HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		return ResponseEntity.ok(couponService.getOrderCoupons(userId, responses));
	}

	@JwtOrderValidate
	@DeleteMapping("/order")
	@Operation(summary = "유저 주문 쿠폰 삭제 요청", description = "유저의 주문 목록에서 적용 가능한 쿠폰을 요청합니다.")
	public ResponseEntity<Void> deleteUserCoupon(@RequestBody DeleteUserCouponRequest request,
		HttpServletRequest httpServletRequest) {
		Long userId = (Long)httpServletRequest.getAttribute(AuthService.USER_ID);
		couponService.deleteUserCoupon(userId, request);
		return ResponseEntity.ok().build();
	}

	// TODO : 쿠폰 환불 처리??
}
