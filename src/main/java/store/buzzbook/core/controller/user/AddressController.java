package store.buzzbook.core.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.dto.user.UpdateAddressRequest;
import store.buzzbook.core.entity.user.Address;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.user.AddressService;

@Tag(name = "회원의 주소 관련 컨트롤러", description = "유저의 주소 정보 추가,삭제,수정,조회 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account/address")
@Slf4j
public class AddressController {
	private final AddressService addressService;

	@PostMapping
	@JwtValidate
	@Operation(summary = "주소 추가", description = "유저의 개인 주소 리스트를 추가한다. user id를 넘겨야한다.")
	public ResponseEntity<Void> createAddress(
		@RequestBody CreateAddressRequest createAddressRequest, HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		addressService.createAddress(createAddressRequest, userId);

		return ResponseEntity.ok().build();
	}

	@JwtValidate
	@DeleteMapping("/{addressId}")
	@Operation(summary = "주소 삭제", description = "유저의 개인 주소 제거. user id를 넘겨야한다.")
	public ResponseEntity<Void> deleteAddress(
		@PathVariable("addressId") Long addressId, HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);

		addressService.deleteAddress(userId, addressId);

		log.debug("주소 삭제 성공 : addressId : {}", addressId);
		return ResponseEntity.ok().build();
	}

	@JwtValidate
	@PutMapping
	@Operation(summary = "주소 수정", description = "유저의 개인 주소 수정.")
	public ResponseEntity<Void> updateAddress(
		@RequestBody UpdateAddressRequest updateAddressRequest, HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);

		addressService.updateAddress(updateAddressRequest, userId);

		return ResponseEntity.ok().build();
	}

	@JwtValidate
	@GetMapping
	@Operation(summary = "주소 리스트 조회", description = "유저의 개인 주소 수정.")
	public ResponseEntity<List<Address>> getAddressList(HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);

		List<Address> addressList = addressService.getAddressList(userId);

		return ResponseEntity.ok().body(addressList);
	}
}
