package store.buzzbook.core.controller.user;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.annotation.JwtValidate;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.user.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "회원 상품 위시리스트 관리 컨트롤러", description = "회원 위시리스트 추가/제거 관리 api")
@RequiredArgsConstructor
public class WishlistController {

	private final WishlistService wishlistService;

	@JwtValidate
	@GetMapping
	@ApiOperation("userId로 위시리스트를 넘긴다.")
	public ResponseEntity<Page<ProductResponse>> getWishlist(HttpServletRequest request,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false) Integer size) {

		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		return ResponseEntity.ok(wishlistService.getWishlists(userId, page, size));
	}

	@JwtValidate
	@PostMapping("/{productId}")
	@ApiOperation("좋아요를 누르면 발생. userId와 상품 id가 포함되어야한다.")
	public ResponseEntity<Long> createWishlist(HttpServletRequest request,
		@PathVariable int productId) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		return ResponseEntity.status(HttpStatus.CREATED).body(wishlistService.createWishlist(userId, productId));
	}

	@JwtValidate
	@DeleteMapping("/{id}")
	@ApiOperation("좋아요된 상태에서 또 좋아요를 누르면 발생. wishlist의 id필요")
	public ResponseEntity<Void> deleteWishlist(HttpServletRequest request,
		@PathVariable long id) {
		wishlistService.deleteWishlist(id);
		return ResponseEntity.noContent().build();
	}

	@JwtValidate
	@GetMapping("/{productId}")
	@ApiOperation("해당 상품이 user가 wishlist에 등록한 상품인지 확인")
	public ResponseEntity<Boolean> checkWishlist(HttpServletRequest request,
		@PathVariable int productId) {
		Long userId = (Long)request.getAttribute(AuthService.USER_ID);
		return ResponseEntity.ok(wishlistService.isUserWishlist(userId, productId));
	}

}
