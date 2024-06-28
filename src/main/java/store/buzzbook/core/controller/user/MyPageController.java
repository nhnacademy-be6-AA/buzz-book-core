package store.buzzbook.core.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.user.UpdateUserRequest;
import store.buzzbook.core.service.user.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account/{userId}/mypage")
@Tag(name = "마이페이지 컨트롤러", description = "유저의 마이페이지, 개인정보 관리 api")
public class MyPageController {
	private static final Logger log = LoggerFactory.getLogger(MyPageController.class);
	private final UserService userService;

	@PatchMapping("/password")
	@Operation(summary = "비밀번호 변경", description = "encoded password만 받는다. 비밀번호 변경용.")
	public ResponseEntity<Void> changePassword(@PathVariable("userId") Long userId) {

		return ResponseEntity.ok().build();
	}

	@PutMapping
	@Operation(summary = "유저 정보 변경", description = "비밀번호를 제외한 일반 개인 정보들을 변경한다.")
	public ResponseEntity<Void> updateUser(@PathVariable("userId") Long userId,
		@RequestBody UpdateUserRequest updateUserRequest) {

		return ResponseEntity.ok().build();
	}

	@PatchMapping("/deactivate")
	@Operation(summary = "탈퇴 요청", description = "탈퇴용 컨트롤러. userId를 넘겨야한다.")
	public ResponseEntity<Void> deactivateUser(@PathVariable("userId") Long userId, @RequestBody String reason) {
		if (!userService.deactivate(userId, reason)) {
			log.debug("탈퇴 처리 오류 id : {}", userId);
			return ResponseEntity.internalServerError().build();
		}

		log.debug("탈퇴 처리 완료 id : {}", userId);
		return ResponseEntity.ok().build();
	}

}
