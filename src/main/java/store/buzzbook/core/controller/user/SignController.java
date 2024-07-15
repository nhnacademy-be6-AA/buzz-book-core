package store.buzzbook.core.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.service.user.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
@Tag(name = "회원가입, 로그인 관련 컨트롤러", description = "유저 인증 관련 api")
@Slf4j
public class SignController {
	private final UserService userService;

	@PostMapping("/login")
	@Operation(summary = "로그인 요청", description = "유저의 로그인 id를 이용해 login id와 encoded password를 준다. ")
	public ResponseEntity<LoginUserResponse> login(@RequestBody String loginId) {

		log.warn("{}", loginId);

		LoginUserResponse loginUserResponse = null;

		loginUserResponse = userService.requestLogin(loginId);

		return ResponseEntity.ok(loginUserResponse);
	}

	@PostMapping("/register")
	@Operation(summary = "회원가입 요청", description = "회원가입 처리용 post 컨트롤러")
	public ResponseEntity<Void> register(@RequestBody RegisterUserRequest registerUserRequest) {
		log.debug("RegisterUserRequest: {}", registerUserRequest);

		userService.requestRegister(registerUserRequest);

		log.debug("유저 회원가입 성공");
		return ResponseEntity.ok().build();
	}

	@PutMapping("/login")
	@Operation(summary = "로그인 성공 처리", description = "로그인 성공시 해당 회원 정보 리턴")
	public ResponseEntity<UserInfo> successLogin(@RequestBody String loginId) {
		UserInfo userInfo = userService.successLogin(loginId);
		return ResponseEntity.ok(userInfo);
	}

	@PutMapping("/activate")
	@Operation(summary = "계정 활성화 요청", description = "휴면상태의 계정 활성화 요청")
	public ResponseEntity<Void> activate(@RequestBody String loginId) {
		userService.activate(loginId);
		return ResponseEntity.ok().build();
	}

}
