package store.buzzbook.core.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.buzzbook.account.dto.user.WishlistRequest;

@RestController
@RequestMapping("/api/wishlist")
@Api(tags = "위시리스트 관리 api")
public class WishlistController {

	@GetMapping("/{userId}")
	@ApiOperation("userId로 위시리스트를 넘긴다.")
	public void getWishlist(@PathVariable("userId") Long userId){
		//좋아요 누른 리스트
	}

	@PostMapping
	@ApiOperation("좋아요를 누르면 발생. userId와 상품 id가 포함되어야한다.")
	public void createWishlist(@RequestBody WishlistRequest createWishlistRequest){
		//좋아요 누름
	}

	@DeleteMapping
	@ApiOperation("좋아요된 상태에서 또 좋아요를 누르면 발생. userId와 상품 id가 포함되어야한다.")
	public void deleteWishlist(@RequestBody WishlistRequest deleteWishlistRequest){
		//좋아요 두번 누름 혹은 삭제
	}



}
