package store.buzzbook.core.repository.user;

import static store.buzzbook.core.entity.cart.QCart.*;
import static store.buzzbook.core.entity.cart.QCartDetail.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.cart.CartNotExistsException;
import store.buzzbook.core.common.util.ZonedDateTimeParser;
import store.buzzbook.core.dto.cart.GetCartResponse;
import store.buzzbook.core.entity.cart.Cart;
import store.buzzbook.core.entity.cart.CartDetail;

@RequiredArgsConstructor
@Transactional
public class CartRepositoryCustomImpl implements CartRepositoryCustom {
	private static final Logger log = LoggerFactory.getLogger(CartRepositoryCustomImpl.class);
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<GetCartResponse> findCartWithCartDetailList(Long cartId) {
		Cart motherCart = jpaQueryFactory
			.select(Projections.fields(Cart.class))
			.from(cart)
			.where(cart.id.eq(cartId))
			.fetchOne();

		if (Objects.isNull(motherCart)) {
			log.warn("존재하지 않는 id의 카트를 조회했습니다. : {}", cartId);
			throw new CartNotExistsException(cartId);
		}

		List<CartDetail> cartDetailList = jpaQueryFactory
			.select(Projections.fields(CartDetail.class))
			.from(cartDetail)
			.where(cartDetail.cart.id.eq(cartId))
			.fetch();

		if (!cartDetailList.isEmpty()) {
			return Optional.of(GetCartResponse.builder()
				.id(motherCart.getId())
				.userId(motherCart.getUser().getId())
				.cartDetailList(cartDetailList)
				.updateDate(ZonedDateTimeParser.toStringDateTime(motherCart.getUpdateDate()))
				.build());
		}

		return Optional.of(GetCartResponse.builder()
			.id(motherCart.getId())
			.userId(motherCart.getId())
			.updateDate(ZonedDateTimeParser.toStringDateTime(motherCart.getUpdateDate()))
			.cartDetailList(List.of())
			.build());
	}
}
