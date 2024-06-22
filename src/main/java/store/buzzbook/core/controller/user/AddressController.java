package store.buzzbook.core.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.service.user.AddressService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/account/{userId}/address")
public class AddressController {
	private static final Logger log = LoggerFactory.getLogger(AddressController.class);
	private final AddressService addressService;

	@ApiOperation("유저의 개인 주소 리스트를 추가한다. user id를 넘겨야한다.")
	public ResponseEntity<?> createAddress(@PathVariable("userId") Long userId,
		@RequestBody CreateAddressRequest createAddressRequest) {

		try {
			addressService.createAddress(createAddressRequest, userId);
		} catch (UserNotFoundException e) {
			log.warn("주소 생성에 실패 했습니다. 회원 id : {}", userId);
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok().build();
	}
}
