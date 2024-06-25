package store.buzzbook.core.controller.user;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.user.DeactivateUserException;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.RegisterUserResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.service.user.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
@Tag(name = "user sign in/up controller", description = "유저 인증 관련 api")
@Slf4j
public class SignController {
	private final UserService userService;

	@PostMapping("/login")
	@Operation(summary = "로그인 요청", description = "유저의 로그인 id를 이용해 login id와 encoded password를 준다. ")
	public ResponseEntity<LoginUserResponse> login(@RequestBody String loginId) {
		LoginUserResponse loginUserResponse = null;
		try {
			loginUserResponse = userService.requestLogin(loginId);

		} catch (UserNotFoundException | DeactivateUserException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		return ResponseEntity.ok(loginUserResponse);
	}

	@PostMapping("/register")
	@Operation(summary = "회원가입 요청", description = "회원가입 처리용 post 컨트롤러")
	public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest registerUserRequest) {
		log.info("RegisterUserRequest: {}", registerUserRequest);
		RegisterUserResponse response;

		try {
			response = userService.requestRegister(registerUserRequest);
		} catch (UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		log.info("유저 회원가입 성공");

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/login")
	@Operation(summary = "로그인 성공 처리", description = "로그인 성공시 해당 회원 정보 리턴")
	public ResponseEntity<UserInfo> successLogin(@RequestBody String loginId) {
		UserInfo userInfo = userService.successLogin(loginId);

		if (Objects.isNull(userInfo)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return ResponseEntity.ok(userInfo);

	}

}
