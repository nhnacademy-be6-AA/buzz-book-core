package store.buzzbook.core.repository.user;

import static store.buzzbook.core.entity.cart.QCart.*;
import static store.buzzbook.core.entity.cart.QCartDetail.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.cart.GetCartResponse;
import store.buzzbook.core.entity.cart.Cart;
import store.buzzbook.core.entity.cart.CartDetail;

@RequiredArgsConstructor
public class CartRepositoryCustomImpl implements CartRepositoryCustom {
	private static final Logger log = LoggerFactory.getLogger(CartRepositoryCustomImpl.class);
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<GetCartResponse> findCartByCartId(Long cartId) {
		Cart motherCart = jpaQueryFactory
			.select(Projections.fields(Cart.class))
			.from(cart)
			.where(cart.id.eq(cartId))
			.fetchOne();

		if (Objects.isNull(motherCart)) {
			return Optional.empty();
		}

		List<CartDetail> cartDetailList = jpaQueryFactory
			.select(Projections.fields(CartDetail.class))
			.from(cartDetail)
			.where(cartDetail.cart.id.eq(cartId))
			.fetch();

		if (cartDetailList.isEmpty()) {
			return Optional.of(GetCartResponse.builder()
				.id(motherCart.getId())
				.userId(motherCart.getUser().getId())
				.cartDetailList(List.of()).build());
		}

		List<CartDetailResponse> cartResponseList = new LinkedList<>();

		cartDetailList.forEach(
			cartDetail -> cartResponseList.add(cartDetail.toResponse(cartDetail.getProduct().getThumbnailPath())));

		return Optional.of(GetCartResponse.builder()
			.id(motherCart.getId())
			.userId(motherCart.getUser().getId())
			.cartDetailList(cartResponseList)
			.build());
	}

	//todo 리팩토링 필요
	@Override
	public Optional<GetCartResponse> findCartByUserId(Long userId) {
		Cart motherCart = jpaQueryFactory
			.select(Projections.fields(Cart.class))
			.from(cart)
			.where(cart.user.id.eq(userId))
			.fetchOne();

		if (Objects.isNull(motherCart)) {
			return Optional.empty();
		}

		long cartId = motherCart.getId();

		List<CartDetail> cartDetailList = jpaQueryFactory
			.select(Projections.fields(CartDetail.class))
			.from(cartDetail)
			.where(cartDetail.cart.id.eq(cartId))
			.fetch();

		if (cartDetailList.isEmpty()) {
			return Optional.of(GetCartResponse.builder()
				.id(motherCart.getId())
				.userId(motherCart.getUser().getId())
				.cartDetailList(List.of()).build());
		}

		List<CartDetailResponse> cartResponseList = new LinkedList<>();

		cartDetailList.forEach(
			cartDetail -> cartResponseList.add(cartDetail.toResponse(cartDetail.getProduct().getThumbnailPath())));

		return Optional.of(GetCartResponse.builder()
			.id(motherCart.getId())
			.userId(motherCart.getUser().getId())
			.cartDetailList(cartResponseList)
			.build());
	}
}
