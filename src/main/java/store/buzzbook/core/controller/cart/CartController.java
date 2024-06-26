package store.buzzbook.core.controller.cart;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import store.buzzbook.core.dto.cart.CreateCartDetailRequest;
import store.buzzbook.core.dto.cart.DeleteCartDetailRequest;
import store.buzzbook.core.service.user.CartService;

@RestController
@RequestMapping("/api/cart")
@Api("장바구니 관련 api")
public class CartController {
	private CartService cartService;

	@GetMapping("/{cartId}")
	@ApiOperation("장바구니 내용을 가져온다. cartId를 넘기되, null 혹은 음수라면 새로운 장바구니로 인식한다.")
	public void getCartByCartId(@PathVariable Long cartId) {
		if (cartId < 0L) {
			cartService.createNewCart(null);
		}

		//todo 비회원 회원 구분을 위한 바디 원함.
		if (cartId > -1L) {
			//cartService.createNewCart()
		}
	}

	@PostMapping
	@ApiOperation("장바구니 내용을 추가한다. cartId, product id를 넘기되, null 혹은 음수라면 새로운 장바구니로 인식한다.")
	public void createCartDetail(@RequestBody CreateCartDetailRequest createCartDetailRequest) {
		/* todo cart id가 null or -1이라면 새로운 cart
		user id가 null or -1이라면 비회원
		카트 내용 변경시 create date 변경

		비회원 + 새로운 카트라면 return으로 프론트엔드에서
		Redis에 새로운 비회원 카트 아이디를 저장하도록 명령.
		 */
	}

	@DeleteMapping
	@ApiOperation("장바구니 내용을 제거한다. cartId와 상품Id를 넘겨 받는다.")
	public void deleteCartDetail(@RequestBody DeleteCartDetailRequest deleteCartDetailRequest) {

	}

	@PutMapping
	@ApiOperation("장바구니 내용을 변경한다. Create와 유사하다. cart id가 null 혹은 음수일 경우 오류로 인식한다.")
	public void updateCartDetail(@RequestBody CreateCartDetailRequest createCartDetailRequest) {

	}
}
