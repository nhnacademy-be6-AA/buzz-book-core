package store.buzzbook.core.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.buzzbook.account.dto.user.LoginUserResponse;
import store.buzzbook.account.dto.user.RegisterUserRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
@Api(tags = "유저 인증 관련 api")
public class SignController {
	//private final UserService accountService;


	@PostMapping("/login")
	@ApiOperation("유저의 로그인 id를 이용해 login id와 encoded password를 준다. ")
	public ResponseEntity<LoginUserResponse> login(@RequestBody String loginId){
		return ResponseEntity.ok(null);
	}

	@PostMapping("/register")
	@ApiOperation("회원가입 처리용 post 컨트롤러")
	public ResponseEntity<?> register(@RequestBody RegisterUserRequest registerUserRequest){

		return ResponseEntity.noContent().build();
	}





}
