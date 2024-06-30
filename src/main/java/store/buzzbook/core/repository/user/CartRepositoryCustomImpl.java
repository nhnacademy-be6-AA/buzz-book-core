package store.buzzbook.core.repository.user;

import static store.buzzbook.core.entity.cart.QCartDetail.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.entity.cart.CartDetail;

@RequiredArgsConstructor
public class CartRepositoryCustomImpl implements CartRepositoryCustom {
	private static final Logger log = LoggerFactory.getLogger(CartRepositoryCustomImpl.class);
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<List<CartDetailResponse>> findCartByCartId(Long cartId) {
		List<CartDetail> cartDetailList = jpaQueryFactory
			.select(Projections.fields(CartDetail.class))
			.from(cartDetail)
			.where(cartDetail.cart.id.eq(cartId))
			.fetch();

		if (cartDetailList.isEmpty()) {
			return Optional.of(List.of());
		}

		List<CartDetailResponse> cartResponseList = new LinkedList<>();

		cartDetailList.forEach(
			cartDetail -> cartResponseList.add(cartDetail.toResponse(cartDetail.getProduct().getThumbnailPath())));

		return Optional.of(cartResponseList);
	}

}
