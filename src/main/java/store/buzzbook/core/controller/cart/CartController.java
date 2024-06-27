package store.buzzbook.core.controller.cart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.cart.CreateCartDetailRequest;
import store.buzzbook.core.dto.cart.GetCartResponse;
import store.buzzbook.core.dto.cart.UpdateCartRequest;
import store.buzzbook.core.service.cart.CartService;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "장바구니 관련 api")
@RequiredArgsConstructor
public class CartController {
	private static final Logger log = LoggerFactory.getLogger(CartController.class);
	private final CartService cartService;

	@GetMapping
	@Operation(summary = "장바구니 조회(비회원)", description = "카트 id로 장바구니 내용을 가져온다.")
	public ResponseEntity<GetCartResponse> getCartByCartId(@RequestParam Long cartId) {
		log.debug("장바구니 아이디로 장바구니 조회 요청 : {}", cartId);

		GetCartResponse response = null;

		response = cartService.getCartByCartId(cartId);

		return ResponseEntity.ok(response);
	}

	@PostMapping
	@Operation(summary = "장바구니 내용물 생성", description = "장바구니 내용을 추가한다. 상품, 상품 갯수 필요")
	public ResponseEntity<Void> createCartDetail(@RequestBody CreateCartDetailRequest createCartDetailRequest) {
		cartService.createCartDetail(createCartDetailRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{cartDetailId}")
	@Operation(summary = "장바구니 물건 제거", description = "장바구니 내용을 제거한다.")
	public ResponseEntity<GetCartResponse> deleteCartDetail(@RequestParam("cartId") Long cartId,
		@PathVariable("cartDetailId") Long cartDetailId) {

		GetCartResponse getCartResponse = cartService.deleteCartDetail(cartId, cartDetailId);

		return ResponseEntity.ok().body(getCartResponse);
	}

	@DeleteMapping
	@Operation(summary = "장바구니 물건 모두 제거", description = "장바구니 내용을 모두 제거한다.")
	public ResponseEntity<Void> deleteAllCartDetail(@RequestParam Long cartDetailId) {
		cartService.deleteAll(cartDetailId);

		return ResponseEntity.ok().build();
	}

	@PutMapping
	@Operation(summary = "장바구니 내용 변경", description = "장바구니 내용을 변경한다. Create와 유사하다.")
	public ResponseEntity<GetCartResponse> updateCartDetail(@RequestBody UpdateCartRequest updateCartRequest) {
		GetCartResponse response = null;

		response = cartService.updateCart(updateCartRequest);

		return ResponseEntity.ok().body(response);
	}
}
