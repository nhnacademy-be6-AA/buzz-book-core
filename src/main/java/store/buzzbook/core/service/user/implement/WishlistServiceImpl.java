package store.buzzbook.core.service.user.implement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.entity.cart.Wishlist;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.repository.user.WishlistRepository;
import store.buzzbook.core.service.user.WishlistService;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

	private final WishlistRepository wishlistRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public long createWishlist(long userId, int productId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		Product product = productRepository.findById(productId).orElseThrow(
			() -> new DataNotFoundException("product", productId));
		Wishlist newWishlist = wishlistRepository.save(new Wishlist(user, product));
		return newWishlist.getId();
	}

	@Override
	@Transactional
	public void deleteWishlist(long wishlistId) {
		wishlistRepository.deleteById(wishlistId);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductResponse> getWishlists(long userId, int pageNo, int pageSize) {

		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException();
		}

		Page<Wishlist> wishlistPage = wishlistRepository.findAllByUserId(userId, PageRequest.of(pageNo, pageSize));

		return (new PageImpl<>(
			productRepository.findAllById(
				wishlistPage.getContent().stream()    // wishlist에서 페이징된 객체 꺼내기
					.map(Wishlist::getProduct)            // 해당 객체에서 product 가져오기
					.map(Product::getId).toList()),        // 해당 product에서 id를 list로 가져오기
			wishlistPage.getPageable(),
			wishlistPage.getTotalPages())).map(ProductResponse::convertToProductResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isUserWishlist(long userId, int productId) {

		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException();
		}

		if (!productRepository.existsById(productId)) {
			throw new DataNotFoundException("product", productId);
		}

		return wishlistRepository.existsByUserIdAndProductId(userId, productId);
	}
}
