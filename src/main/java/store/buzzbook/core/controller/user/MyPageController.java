package store.buzzbook.core.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import store.buzzbook.core.dto.user.ChangePasswordRequest;
import store.buzzbook.core.dto.user.DeactivateUserRequest;
import store.buzzbook.core.dto.user.UpdateUserRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.user.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account/mypage/")
@Tag(name = "마이페이지 컨트롤러", description = "유저의 마이페이지, 개인정보 관리 api")
@Slf4j
public class MyPageController {
	private final UserService userService;
	
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
	public ResponseEntity<UserInfo> updateUser(@RequestBody UpdateUserRequest updateUserRequest,
		HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		UserInfo userInfo = userService.updateUserInfo(userId, updateUserRequest);

		return ResponseEntity.ok().body(userInfo);
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
	ResponseEntity<UserInfo> getUserInfo(HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		UserInfo userInfo = userService.getUserInfoByUserId(userId);

		return ResponseEntity.ok().body(userInfo);
	}

}
