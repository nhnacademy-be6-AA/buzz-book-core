package store.buzzbook.core.service.user.implement;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.cart.CartNotExistsException;
import store.buzzbook.core.common.util.ZonedDateTimeParser;
import store.buzzbook.core.dto.cart.GetCartResponse;
import store.buzzbook.core.entity.cart.Cart;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.user.CartRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.CartService;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
	private CartRepository cartRepository;
	private UserRepository userRepository;

	@Override
	public GetCartResponse getCartByCartId(Long cartId) {
		GetCartResponse getCartResponse;

		try {
			getCartResponse = cartRepository.findCartWithCartDetailList(cartId).orElse(null);
		} catch (CartNotExistsException e) {
			Cart newCart = cartRepository.save(Cart.builder().build());
			log.info("카트를 새로이 생성합니다. : {}", newCart.getId());
			return GetCartResponse.builder()
				.id(newCart.getId())
				.updateDate(ZonedDateTimeParser.toStringDateTime(ZonedDateTime.now()))
				.cartDetailList(List.of())
				.build();
		}

		return getCartResponse;
	}

	@Override
	public GetCartResponse createNewCart(Long userId) {

		User fkUser = null;
		if (Objects.nonNull(userId)) {
			fkUser = userRepository.getReferenceById(userId);
		}

		Cart savedCart = cartRepository.save(
			Cart.builder().updateDate(ZonedDateTime.now()).user(fkUser).build());

		return GetCartResponse.builder()
			.id(savedCart.getId())
			.updateDate(ZonedDateTimeParser.toStringDateTime(savedCart.getUpdateDate()))
			.cartDetailList(List.of())
			.userId(userId).build();
	}
}
