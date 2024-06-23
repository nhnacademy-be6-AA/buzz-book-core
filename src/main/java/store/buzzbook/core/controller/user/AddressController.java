package store.buzzbook.core.controller.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.dto.user.UpdateAddressRequest;
import store.buzzbook.core.entity.user.Address;
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
			return ResponseEntity.badRequest().build();
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

	@PutMapping
	@ApiOperation("유저의 개인 주소 수정.")
	public ResponseEntity<Void> updateAddress(@PathVariable("userId") Long userId,
		@RequestBody UpdateAddressRequest updateAddressRequest) {
		try {
			addressService.updateAddress(updateAddressRequest, userId);
		} catch (UserNotFoundException e) {
			log.warn("알 수 없는 유저의 주소 수정 요청. : {}", userId);
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok().build();
	}

	@GetMapping
	@ApiOperation("유저의 개인 주소 수정.")
	public ResponseEntity<List<Address>> getAddressList(@PathVariable("userId") Long userId) {
		List<Address> addressList;
		try {
			addressList = addressService.getAddressList(userId);
		} catch (UserNotFoundException e) {
			log.warn("알 수 없는 유저의 주소 수정 요청. : {}", userId);
			return ResponseEntity.badRequest().build();
		}

		if (addressList.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok().body(addressList);
	}
}
