package store.buzzbook.core.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.dto.user.UpdateUserRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account/{userId}/mypage")
@Api(tags = "유저의 마이페이지, 개인정보 관리 api")
public class MyPageController {
	//private final UserService userService;


	@PatchMapping("/password")
	@ApiOperation("encoded password만 받는다. 비밀번호 변경용. userId를 넘겨야한다.")
	public ResponseEntity<?> changePassword(@PathVariable("userId") Long userId){

		return ResponseEntity.ok().build();
	}

	@PutMapping
	@ApiOperation("비밀번호를 제외한 일반 개인 정보들을 변경한다. userId를 넘겨야한다. ")
	public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequest updateUserRequest){
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	@ApiOperation("탈퇴용 컨트롤러. userId를 넘겨야한다.")
	public ResponseEntity<?> deactivateAccount(@PathVariable("userId") Long userId){
		return ResponseEntity.ok().build();
	}

	@PostMapping("/address")
	@ApiOperation("유저의 개인 주소 리스트를 추가한다. user id를 넘겨야한다.")
	public ResponseEntity<?> createAddress(@PathVariable("userId") Long userId, @RequestBody CreateAddressRequest createAddressRequest){
		return ResponseEntity.ok().build();
	}


	@DeleteMapping("/address")
	@ApiOperation("개인 주소의 id로 유저의 개인 주소 리스트 중 하나 삭제. ")
	public ResponseEntity<?> deleteAddress(@PathVariable("userId") Long userId, @RequestBody Long addressId){
		return ResponseEntity.ok().build();
	}

}
