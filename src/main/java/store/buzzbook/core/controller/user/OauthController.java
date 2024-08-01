package store.buzzbook.core.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.OauthRegisterRequest;
import store.buzzbook.core.service.user.UserAuthService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth2")
public class OauthController {
	private final UserAuthService userAuthService;

	@GetMapping("/register")
	public ResponseEntity<Boolean> isRegistered(@RequestParam String provideId, @RequestParam String provider) {
		Boolean registered = userAuthService.isRegistered(provideId, provider);
		return ResponseEntity.ok(registered);
	}

	@GetMapping("/login")
	public ResponseEntity<LoginUserResponse> requestLogin(@RequestParam String provideId,
		@RequestParam String provider) {
		LoginUserResponse loginUserResponse = userAuthService.getLoginUser(provideId, provider);
		return ResponseEntity.ok(loginUserResponse);
	}

	@PostMapping("/register")
	public ResponseEntity<Void> registerUser(@Valid @RequestBody OauthRegisterRequest registerRequest) {
		userAuthService.register(registerRequest);
		return ResponseEntity.ok().build();
	}

}
