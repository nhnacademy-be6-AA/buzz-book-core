package store.buzzbook.core.repository.order;

import static store.buzzbook.core.entity.order.QOrder.*;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;

import store.buzzbook.core.common.util.FunctionUtil;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.entity.order.Order;

public class OrderQuerydslRepositoryImpl extends QuerydslRepositorySupport implements OrderQuerydslRepository {
	public OrderQuerydslRepositoryImpl() {
		super(Order.class);
	}

	@Override
	public Page<Order> findAllByUser_LoginId(ReadOrderRequest request, Pageable pageable) {
		String[] sortBy = request.getSortBy();
		Boolean[] sortDesc = request.getSortDesc();
		List<OrderSpecifier> orderBy = new LinkedList<>();
		searchOrderMultiSortFilter(sortBy, sortDesc, orderBy);

		QueryResults<Order> results = from(order)
			.where(order.user.userPk.loginId.eq(request.getLoginId()))
			.fetchResults();

		List<Order> content = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<Order> findAll(ReadOrderRequest request, Pageable pageable) {
		String[] sortBy = request.getSortBy();
		Boolean[] sortDesc = request.getSortDesc();
		List<OrderSpecifier> orderBy = new LinkedList<>();
		searchOrderMultiSortFilter(sortBy, sortDesc, orderBy);

		QueryResults<Order> results = from(order)
			.fetchResults();

		List<Order> content = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(content, pageable, total);
	}

	private static void searchOrderMultiSortFilter(String[] sortBy, Boolean[] sortDesc, List<OrderSpecifier> orderBy) {
		for (int i = 0; i < sortBy.length; i++) {
			String key = sortBy[i];
			Boolean value = sortDesc[i];

			switch (key) {
				case "price" -> FunctionUtil.orderDescFilter(orderBy, value, order, "price");
				case "deliveryPolicyId" -> FunctionUtil.orderDescFilter(orderBy, value, order, "deliveryPolicyId");
				case "loginId" -> FunctionUtil.orderDescFilter(orderBy, value, order, "loginId");
				default -> {
				}
			}
		}
	}
}
