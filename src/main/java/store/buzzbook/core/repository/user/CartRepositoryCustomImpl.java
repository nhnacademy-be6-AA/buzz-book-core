package store.buzzbook.core.repository.user;

import static store.buzzbook.core.entity.cart.QCartDetail.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.entity.cart.CartDetail;

@RequiredArgsConstructor
public class CartRepositoryCustomImpl implements CartRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<List<CartDetailResponse>> findCartDetailByCartId(Long cartId) {
		List<CartDetail> cartDetailList = jpaQueryFactory
			.selectFrom(cartDetail)
			.where(cartDetail.cart.id.eq(cartId))
			.fetch();

		if (cartDetailList.isEmpty()) {
			return Optional.of(List.of());
		}

		List<CartDetailResponse> cartResponseList = new LinkedList<>();

		toResponseList(cartDetailList, cartResponseList);

		return Optional.of(cartResponseList);
	}

	@Override
	public Optional<List<CartDetailResponse>> findCartDetailByUuid(byte[] uuid) {
		List<CartDetail> cartDetailList = jpaQueryFactory
			.selectFrom(cartDetail)
			.where(cartDetail.cart.uuid.eq(uuid))
			.fetch();

		if (cartDetailList.isEmpty()) {
			return Optional.of(List.of());
		}

		List<CartDetailResponse> cartResponseList = new LinkedList<>();
		toResponseList(cartDetailList, cartResponseList);

		return Optional.of(cartResponseList);
	}

	private void toResponseList(List<CartDetail> cartDetailList, List<CartDetailResponse> cartResponseList) {
		List<Long> forDeleteIds = new LinkedList<>();

		cartDetailList.forEach(
			cartDetail -> {
				if (cartDetail.getProduct().getStock() <= 0) {
					forDeleteIds.add(cartDetail.getId());
				} else {
					cartResponseList.add(cartDetail.toResponse());
				}
			});

		if (!forDeleteIds.isEmpty()) {
			jpaQueryFactory.delete(cartDetail)
				.where(cartDetail.cart.id.in(forDeleteIds))
				.execute();
		}
	}

}
