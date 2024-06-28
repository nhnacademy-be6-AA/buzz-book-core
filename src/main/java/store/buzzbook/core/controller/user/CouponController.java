package store.buzzbook.core.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.coupon.CreateUserCouponRequest;
import store.buzzbook.core.service.user.UserService;

@Tag(name = "회원의 쿠폰 관련 컨트롤러", description = "유저의 쿠폰 조회, 추가 관리")
@RestController
@RequestMapping("/api/account/coupons")
@RequiredArgsConstructor
public class CouponController {

	private final UserService userService;

	@PostMapping
	@Operation(summary = "쿠폰 추가", description = "유저의 쿠폰 리스트를 추가한다. user id 와 coupon id 를 넘겨야한다.")
	public ResponseEntity<Void> createUserCoupon(@Valid @RequestBody CreateUserCouponRequest request) {
		userService.addUserCoupon(request);
		return ResponseEntity.ok().build();
	}
}
