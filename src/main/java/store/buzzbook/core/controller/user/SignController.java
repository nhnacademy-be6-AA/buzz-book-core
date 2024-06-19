package store.buzzbook.core.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.buzzbook.core.dto.user.RegisterUserResponse;
import store.buzzbook.core.service.user.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
@Api(tags = "유저 인증 관련 api")
@Slf4j
public class SignController {
	private final UserService userService;


	@PostMapping("/login")
	@ApiOperation("유저의 로그인 id를 이용해 login id와 encoded password를 준다. ")
	public ResponseEntity<LoginUserResponse> login(@RequestBody String loginId){
		return ResponseEntity.ok(null);
	}




	@PostMapping("/register")
	@ApiOperation("회원가입 처리용 post 컨트롤러")
	public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest registerUserRequest){
		RegisterUserResponse response;

		try {
			response =userService.requestRegister(registerUserRequest);
		}catch (UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		log.info("유저 회원가입 성공");

		return ResponseEntity.ok(response);
	}





}
