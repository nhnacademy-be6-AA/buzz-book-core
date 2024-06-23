package store.buzzbook.core.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.service.user.AddressService;

@Api(tags = "유저의 주소 정보 관리")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/account/{userId}/address")
public class AddressController {
	private static final Logger log = LoggerFactory.getLogger(AddressController.class);
	private final AddressService addressService;

	@PostMapping
	@ApiOperation("유저의 개인 주소 리스트를 추가한다. user id를 넘겨야한다.")
	public ResponseEntity<Void> createAddress(@PathVariable("userId") Long userId,
		@RequestBody CreateAddressRequest createAddressRequest) {

		try {
			addressService.createAddress(createAddressRequest, userId);
		} catch (UserNotFoundException e) {
			log.warn("주소 생성에 실패 했습니다. 회원 id : {}", userId);
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{addressId}")
	@ApiOperation("유저의 개인 주소 제거. user id를 넘겨야한다.")
	public ResponseEntity<Void> deleteAddress(@PathVariable("userId") Long userId,
		@PathVariable("addressId") Long addressId) {
		try {
			addressService.deleteAddress(userId, addressId);

		} catch (UserNotFoundException e) {
			log.warn("주소 삭제 실패 : addressId : {}", addressId);
			return ResponseEntity.badRequest().build();
		}

		log.info("주소 삭제 성공 : addressId : {}", addressId);
		return ResponseEntity.ok().build();
	}
}
