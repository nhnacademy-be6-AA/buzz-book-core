package store.buzzbook.core.service.cart.implement;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.cart.CartNotExistsException;
import store.buzzbook.core.common.util.UuidUtil;
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
@Slf4j
public class CartServiceImpl implements CartService {
	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final CartDetailRepository cartDetailRepository;

	@Transactional(readOnly = true)
	@Override
	public List<CartDetailResponse> getCartByUuId(String uuid) {
		Optional<List<CartDetailResponse>> cartResponseOptional = cartRepository.findCartDetailByUuid(
			UuidUtil.stringToByte(uuid));

		if (cartResponseOptional.isEmpty()) {
			log.debug("존재하지 않는 장바구니 id로 조회를 요청했습니다. id : {}", uuid);
			throw new CartNotExistsException(uuid);
		}

		List<CartDetailResponse> cartDetailResponses = cartResponseOptional.orElseGet(List::of);

		if (!cartDetailResponses.isEmpty()) {
			orderByPrice(cartDetailResponses);
		}

		return cartDetailResponses;
	}

	@Transactional(readOnly = true)
	@Override
	public List<CartDetailResponse> getCartByUserId(Long userId) {
		Optional<Cart> cart = cartRepository.findCartByUserId(userId);

		if (cart.isEmpty()) {
			log.debug("존재하지 않는 회원 id로 조회를 요청했습니다. id : {}", userId);
			createCart(userId);
			return List.of();
		}

		Optional<List<CartDetailResponse>> cartResponse = cartRepository.findCartDetailByCartId(cart.get().getId());
		List<CartDetailResponse> cartDetailResponses = cartResponse.orElseGet(List::of);

		if (!cartDetailResponses.isEmpty()) {
			orderByPrice(cartDetailResponses);
		}

		return cartDetailResponses;
	}

	@Transactional
	@Override
	public void createCartDetail(String uuid, CreateCartDetailRequest createCartDetailRequest) {
		Optional<Cart> cart = cartRepository.findCartByUuid(UuidUtil.stringToByte(uuid));
		int productId = createCartDetailRequest.productId();

		if (cart.isEmpty()) {
			throw new CartNotExistsException(uuid);
		}

		Product product = productRepository.getReferenceById(productId);
		Optional<CartDetail> existDetailOptional = cartDetailRepository.findByProductIdAndCartId(productId,
			cart.get().getId());

		if (existDetailOptional.isPresent()) {
			existDetailOptional.get().changeQuantity(createCartDetailRequest.quantity());
			cartDetailRepository.save(existDetailOptional.get());
		} else {
			cartDetailRepository.save(createCartDetailRequest.toCartDetail(cart.get(), product));
		}

	}

	@Transactional
	@Override
	public List<CartDetailResponse> deleteCartDetail(String uuid, Long cartDetailId) {
		cartDetailRepository.deleteById(cartDetailId);

		Optional<List<CartDetailResponse>> cartResponse = cartRepository.findCartDetailByUuid(
			UuidUtil.stringToByte(uuid));

		List<CartDetailResponse> cartDetailResponses = cartResponse.orElseGet(List::of);

		if (!cartDetailResponses.isEmpty()) {
			orderByPrice(cartDetailResponses);
		}

		return cartDetailResponses;
	}

	@Transactional
	@Override
	public void deleteAll(String uuid) {
		Optional<Cart> cart = cartRepository.findCartByUuid(UuidUtil.stringToByte(uuid));

		if (cart.isEmpty()) {
			throw new CartNotExistsException(uuid);
		}

		cartDetailRepository.deleteAllByCart(cart.get());
	}

	@Transactional
	@Override
	public void updateCartDetail(Long detailId, Integer quantity) {
		Optional<CartDetail> cartDetailOptional = cartDetailRepository.findById(detailId);

		if (cartDetailOptional.isEmpty()) {
			log.debug("존재하지 않는 장바구니 상세 id의 업데이트 요청 : {}", detailId);
			throw new CartNotExistsException(detailId);
		}

		cartDetailOptional.get().changeQuantity(quantity);

		cartDetailRepository.save(cartDetailOptional.get());
	}

	@Override
	public boolean isValidUUID(String uuid) {
		return cartRepository.existsByUuid(UuidUtil.stringToByte(uuid));
	}

	@Override
	public boolean isValidUUID(String uuid, Long userId) {
		Optional<Cart> userCart = cartRepository.findCartByUserId(userId);

		if (userCart.isEmpty()) {
			throw new CartNotExistsException(userId);
		}

		return false;
	}

	@Override
	public String createCart(Long userId) {
		User fkUser = null;
		if (Objects.nonNull(userId)) {
			log.debug("회원으로 장바구니를 생성합니다. id : {}", userId);
			fkUser = userRepository.getReferenceById(userId);
		}

		Cart savedCart = cartRepository.save(
			Cart.builder().user(fkUser).uuid(UuidUtil.createUuidToByte()).build());

		return UuidUtil.uuidByteToString(savedCart.getUuid());
	}

	@Transactional
	@Override
	public String createCart() {
		Cart savedCart = cartRepository.save(
			Cart.builder().user(null).uuid(UuidUtil.createUuidToByte()).build());

		return UuidUtil.uuidByteToString(savedCart.getUuid());
	}

	@Transactional
	@Override
	public String getUuidByUserId(Long userId) {
		Optional<Cart> cart = cartRepository.findCartByUserId(userId);

		if (cart.isEmpty()) {
			log.debug("해당 유저의 장바구니는 존재하지 않습니다. 자동으로 생성합니다. : {}", userId);
			return createCart(userId);
		} else {
			return UuidUtil.uuidByteToString(cart.get().getUuid());
		}
	}

	private boolean isValidUuid(String uuid, Long userId) {
		Optional<Cart> cart = cartRepository.findCartByUuid(UuidUtil.stringToByte(uuid));

		if (cart.isEmpty() || !cart.get().getUser().getId().equals(userId)) {
			log.debug("유효하지 않은 uuid입니다.");
			return false;
		}

		return true;
	}

	private boolean isValidUuid(String uuid) {
		Optional<Cart> cart = cartRepository.findCartByUuid(UuidUtil.stringToByte(uuid));

		if (cart.isEmpty()) {
			log.debug("유효하지 않은 uuid입니다.");
			return false;
		}

		return true;
	}

	private void orderByPrice(List<CartDetailResponse> cartList) {
		cartList.sort((cartA, cartB) -> {
			int priceA = cartA.getPrice() * cartA.getQuantity();
			int priceB = cartB.getPrice() * cartB.getQuantity();
			return Integer.compare(priceB, priceA);  // 내림차순으로 정렬
		});
	}

}
