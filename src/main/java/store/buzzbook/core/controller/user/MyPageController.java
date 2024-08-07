package store.buzzbook.core.controller.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.annotation.JwtValidate;
import store.buzzbook.core.dto.coupon.CouponResponse;
import store.buzzbook.core.dto.point.PointLogResponse;
import store.buzzbook.core.dto.user.ChangePasswordRequest;
import store.buzzbook.core.dto.user.DeactivateUserRequest;
import store.buzzbook.core.dto.user.UpdateUserRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.coupon.CouponService;
import store.buzzbook.core.service.point.PointService;
import store.buzzbook.core.service.user.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account/mypage")
@Tag(name = "마이페이지 컨트롤러", description = "유저의 마이페이지, 개인정보 관리 api")
@Slf4j
public class MyPageController {
	private final UserService userService;
	private final PointService pointService;
	private final CouponService couponService;

	@JwtValidate
	@PutMapping("/password")
	@Operation(summary = "비밀번호 변경", description = "encoded password만 받는다. 비밀번호 변경용.")
	public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
		HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);

		userService.updatePassword(userId, changePasswordRequest);

		return ResponseEntity.ok().build();
	}

	@JwtValidate
	@PutMapping
	@Operation(summary = "유저 정보 변경", description = "비밀번호를 제외한 일반 개인 정보들을 변경한다.")
	public ResponseEntity<Void> updateUser(@RequestBody UpdateUserRequest updateUserRequest,
		HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		userService.updateUserInfo(userId, updateUserRequest);

		return ResponseEntity.ok().build();
	}

	@JwtValidate
	@PutMapping("/deactivate")
	@Operation(summary = "탈퇴 요청", description = "탈퇴용 컨트롤러. userId를 넘겨야한다.")
	public ResponseEntity<Void> deactivateUser(@RequestBody DeactivateUserRequest deactivateUserRequest,
		HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		userService.deactivate(userId, deactivateUserRequest);

		log.debug("탈퇴 처리 완료 id : {}", userId);
		return ResponseEntity.ok().build();
	}

	@JwtValidate
	@GetMapping
	@Operation(summary = "유저 정보 요청", description = "유저정보를 넘겨주는 컨트롤러. 인증 인가된 토큰만 가능")
	ResponseEntity<UserInfo> getUserInfo(HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		UserInfo userInfo = userService.getUserInfoByUserId(userId);

		return ResponseEntity.ok().body(userInfo);
	}

	@JwtValidate
	@PostMapping("/coupons")
	@Operation(summary = "회원의 쿠폰 로그 조회 요청", description = "회원의 쿠폰 발급내역, 사용내역을 조회 합니다.")
	public ResponseEntity<List<CouponResponse>> getUserCoupons(
		@RequestBody String couponStatusName,
		HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);

		return ResponseEntity.ok(couponService.getUserCoupons(userId, couponStatusName));
	}

	@JwtValidate
	@GetMapping("/points")
	@Operation(summary = "회원의 포인트 로그 조회 요청", description = "회원의 포인트 적립내역, 사용내역을 조회 합니다.")
	public ResponseEntity<Page<PointLogResponse>> getPointLogs(Pageable pageable, HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);

		return ResponseEntity.ok(pointService.getPointLogs(pageable, userId));
	}

}
