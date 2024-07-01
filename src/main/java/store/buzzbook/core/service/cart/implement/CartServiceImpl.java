package store.buzzbook.core.service.cart.implement;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.cart.CartNotExistsException;
import store.buzzbook.core.dto.cart.CreateCartDetailRequest;
import store.buzzbook.core.dto.cart.GetCartResponse;
import store.buzzbook.core.dto.cart.UpdateCartRequest;
import store.buzzbook.core.entity.cart.Cart;
import store.buzzbook.core.entity.cart.CartDetail;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.CartDetailRepository;
import store.buzzbook.core.repository.user.CartRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.cart.CartService;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final CartDetailRepository cartDetailRepository;

	@Transactional(readOnly = true)
	@Override
	public GetCartResponse getCartByCartId(Long cartId) {

		Optional<GetCartResponse> cartResponseOptional = cartRepository.findCartByCartId(cartId);

		if (cartResponseOptional.isEmpty()) {
			log.debug("존재하지 않는 장바구니 id로 조회를 요청했습니다. id : {}", cartId);
			throw new CartNotExistsException(cartId);
		}

		return cartResponseOptional.get();
	}

	@Transactional(readOnly = true)
	@Override
	public GetCartResponse getCartByUserId(Long userId) {

		Optional<GetCartResponse> cartResponseOptional = cartRepository.findCartByUserId(userId);

		if (cartResponseOptional.isEmpty()) {
			log.debug("존재하지 않는 회원 id로 조회를 요청했습니다. id : {}", userId);
			throw new CartNotExistsException(userId);
		}

		return cartResponseOptional.get();
	}

	@Override
	public GetCartResponse updateCart(UpdateCartRequest updateCartRequest) {
		return null;
	}
	//
	// @Override
	// public GetCartResponse updateCart(UpdateCartRequest updateCartRequest) {
	// 	Optional<CartDetail> cartDetailOptional = cartDetailRepository.findById(updateCartRequest.id());
	//
	// 	if (cartDetailOptional.isEmpty()) {
	// 		log.debug("잘못된 id로 장바구니 상세 변경 요청입니다. : {}", updateCartRequest.id());
	// 		throw new IllegalArgumentException();
	// 	}
	//
	// 	cartDetailOptional.get().changeQuantity(updateCartRequest.quantity());
	//
	// 	cartDetailRepository.save(cartDetailOptional.get());
	// 	Optional<GetCartResponse> cart = cartRepository.findCartByCartId(updateCartRequest.cartId());
	//
	// 	if (cart.isEmpty()) {
	// 		log.debug("잘못된 id로 장바구니 find 실패했습니다. : {}", updateCartRequest.cartId());
	// 		throw new CartNotExistsException(updateCartRequest.cartId());
	// 	}
	//
	// 	return cart.get();
	// }

	@Transactional
	@Override
	public void createCartDetail(CreateCartDetailRequest createCartDetailRequest) {

		Cart cart = cartRepository.getReferenceById(createCartDetailRequest.cartId());
		Product product = productRepository.getReferenceById(createCartDetailRequest.productId());
		cartDetailRepository.save(createCartDetailRequest.toCartDetail(cart, product));

	}

	@Transactional
	@Override
	public GetCartResponse deleteCartDetail(Long cartId, Long cartDetailId) {
		cartDetailRepository.deleteById(cartDetailId);
		Optional<GetCartResponse> getCartResponseOptional = cartRepository.findCartByCartId(cartId);

		return getCartResponseOptional.orElse(null);
	}

	@Transactional
	@Override
	public void deleteAll(Long cartId) {
		Cart cart = cartRepository.getReferenceById(cartId);
		cartRepository.deleteById(cartId);
		cartDetailRepository.deleteByCart(cart);
	}

	@Transactional
	@Override
	public GetCartResponse createCart(Long userId) {

		User fkUser = null;
		if (Objects.nonNull(userId)) {
			log.debug("회원으로 장바구니를 생성합니다. id : {}", userId);
			fkUser = userRepository.getReferenceById(userId);
		}

		Cart savedCart = cartRepository.save(
			Cart.builder().user(fkUser).build());

		return GetCartResponse.builder()
			.id(savedCart.getId())
			.cartDetailList(List.of())
			.userId(userId).build();
	}

}
