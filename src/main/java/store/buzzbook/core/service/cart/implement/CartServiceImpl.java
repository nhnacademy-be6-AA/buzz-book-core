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
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.cart.CreateCartDetailRequest;
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
	public List<CartDetailResponse> getCartByCartId(Long cartId) {
		Optional<List<CartDetailResponse>> cartResponseOptional = cartRepository.findCartByCartId(cartId);

		if (cartResponseOptional.isEmpty()) {
			log.debug("존재하지 않는 장바구니 id로 조회를 요청했습니다. id : {}", cartId);
			throw new CartNotExistsException(cartId);
		}

		return cartResponseOptional.get();
	}

	@Transactional(readOnly = true)
	@Override
	public List<CartDetailResponse> getCartByUserId(Long userId) {
		Optional<Cart> cart = cartRepository.findCartByUserId(userId);

		if (cart.isEmpty()) {
			log.debug("존재하지 않는 회원 id로 조회를 요청했습니다. id : {}", userId);
			throw new CartNotExistsException(userId);
		}

		Optional<List<CartDetailResponse>> cartResponse = cartRepository.findCartByCartId(cart.get().getId());

		if (cartResponse.isEmpty()) {
			return List.of();
		}

		return cartResponse.get();
	}

	@Transactional
	@Override
	public void createCartDetail(Long cartId, CreateCartDetailRequest createCartDetailRequest) {
		Cart cart = cartRepository.getReferenceById(cartId);
		Product product = productRepository.getReferenceById(createCartDetailRequest.productId());
		cartDetailRepository.save(createCartDetailRequest.toCartDetail(cart, product));

	}

	@Transactional
	@Override
	public List<CartDetailResponse> deleteCartDetail(Long cartId, Long cartDetailId) {
		cartDetailRepository.deleteById(cartDetailId);

		Optional<List<CartDetailResponse>> cartResponse = cartRepository.findCartByCartId(cartId);

		return cartResponse.orElse(null);
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
	public void updateCartDetail(Long cartId, Long detailId, Integer quantity) {
		Optional<CartDetail> cartDetailOptional = cartDetailRepository.findById(detailId);

		if (cartDetailOptional.isEmpty()) {
			log.debug("존재하지 않는 장바구니 상세 id의 업데이트 요청 : {}", detailId);
			throw new CartNotExistsException(detailId);
		}

		cartDetailOptional.get().changeQuantity(quantity);

		cartDetailRepository.save(cartDetailOptional.get());
	}

	@Transactional
	@Override
	public Long createCart(Long userId) {

		User fkUser = null;
		if (Objects.nonNull(userId)) {
			log.debug("회원으로 장바구니를 생성합니다. id : {}", userId);
			fkUser = userRepository.getReferenceById(userId);
		}

		Cart savedCart = cartRepository.save(
			Cart.builder().user(fkUser).build());

		return savedCart.getId();
	}

	@Transactional
	@Override
	public Long createCart() {
		Cart savedCart = cartRepository.save(
			Cart.builder().user(null).build());

		return savedCart.getId();
	}

	@Transactional
	@Override
	public Long getCartIdByUserId(Long userId) {

		Optional<Cart> cart = cartRepository.findCartByUserId(userId);

		if (cart.isEmpty()) {
			log.debug("해당 유저의 장바구니는 존재하지 않습니다. 자동으로 생성합니다. : {}", userId);
			return createCart(userId);
		} else {
			return cart.get().getId();
		}
	}

}
