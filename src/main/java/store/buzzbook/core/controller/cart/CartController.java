package store.buzzbook.core.controller.cart;

import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.cart.CreateCartDetailRequest;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.cart.CartService;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "장바구니 관련 api")
@RequiredArgsConstructor
@Slf4j
public class CartController {
	private final CartService cartService;

	@GetMapping
	@Operation(summary = "장바구니 조회(비회원/회원)", description = "카트 id로 장바구니 내용을 가져온다.")
	public ResponseEntity<List<CartDetailResponse>> getCartByUuid(@RequestParam String uuid,
		HttpServletRequest request) {
		log.debug("장바구니 아이디로 장바구니 조회 요청 : {}", uuid);

		//비회원
		if (Objects.isNull(request.getAttribute(AuthService.USER_ID))) {
			List<CartDetailResponse> detailList = cartService.getCartByUuId(uuid);
			return ResponseEntity.ok().body(detailList);
		}

		//회원
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		List<CartDetailResponse> detailList = cartService.getCartByUserId(userId);

		return ResponseEntity.ok(detailList);
	}

	@PostMapping("/detail")
	@Operation(summary = "장바구니 내용물 생성", description = "장바구니 내용을 추가한다. 상품, 상품 갯수 필요")
	public ResponseEntity<Void> createCartDetail(@RequestParam String uuid,
		@RequestBody CreateCartDetailRequest createCartDetailRequest) {
		cartService.createCartDetail(uuid, createCartDetailRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/detail/{detailId}")
	@Operation(summary = "장바구니 물건 제거", description = "장바구니 내용을 제거한다.")
	public ResponseEntity<List<CartDetailResponse>> deleteCartDetail(@RequestParam String uuid,
		@PathVariable("detailId") Long detailId) {
		List<CartDetailResponse> cartResponse = cartService.deleteCartDetail(uuid, detailId);

		return ResponseEntity.ok().body(cartResponse);
	}

	@DeleteMapping
	@Operation(summary = "장바구니 물건 모두 제거", description = "장바구니 내용을 모두 제거한다.")
	public ResponseEntity<Void> deleteAllCartDetail(@RequestParam String uuid) {
		cartService.deleteAll(uuid);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/detail/{detailId}")
	@Operation(summary = "장바구니 내용 변경", description = "장바구니 내용을 변경한다. Create와 유사하다.")
	public ResponseEntity<Void> updateCartDetail(@RequestParam String uuid,
		@PathVariable Long detailId, @RequestParam Integer quantity) {
		cartService.updateCartDetail(detailId, quantity);

		return ResponseEntity.ok().build();
	}

	//회원만 가능
	@Operation(summary = "회원id로 cart uuid 조회", description = "(jwt)회원id로 cart uuid 조회한다. 회원만 가능하다.")
	@GetMapping("/uuid")
	public ResponseEntity<String> getUuidByUserId(HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		String uuid = cartService.getUuidByUserId(userId);

		return ResponseEntity.ok(uuid);
	}

	@GetMapping("/guest")
	public ResponseEntity<String> createCart() {
		String createdUuid = cartService.createCart();

		return ResponseEntity.ok(createdUuid);
	}
}
