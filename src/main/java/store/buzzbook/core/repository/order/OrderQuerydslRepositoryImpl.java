package store.buzzbook.core.repository.order;

import static store.buzzbook.core.entity.order.QOrder.*;
import static store.buzzbook.core.entity.order.QOrderDetail.*;
import static store.buzzbook.core.entity.user.QUser.*;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.util.FunctionUtil;
import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderResponse;

@RequiredArgsConstructor
public class OrderQuerydslRepositoryImpl implements OrderQuerydslRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<ReadOrderProjectionResponse> findAllByUser_LoginId(ReadOrderRequest request, Pageable pageable) {
		String[] sortBy = request.getSortBy();
		Boolean[] sortDesc = request.getSortDesc();
		List<OrderSpecifier> orderBy = new LinkedList<>();
		searchOrderMultiSortFilter(sortBy, sortDesc, orderBy);

		List<ReadOrderProjectionResponse> results = jpaQueryFactory
			.select(
				Projections.constructor(
					ReadOrderProjectionResponse.class,
					order.id.as("id"),
					order.orderStr.as("orderStr"),
					order.user.loginId.as("loginId"),
					order.price.as("price"),
					order.request.as("request"),
					order.address.as("address"),
					order.addressDetail.as("addressDetail"),
					order.zipcode.as("zipcode"),
					order.desiredDeliveryDate.as("desiredDeliveryDate"),
					order.receiver.as("receiver"),
					Projections.fields(
						ReadOrderDetailProjectionResponse.class,
						orderDetail.id.as("orderDetailId"),
						orderDetail.price.as("orderDetailPrice"),
						orderDetail.quantity.as("orderDetailQuantity"),
						ExpressionUtils.as(
							Expressions.stringTemplate(
								"CASE WHEN {0} = true THEN 'true' ELSE 'false' END",
								orderDetail.wrap
							),
							"orderDetailWrap"
						),
						orderDetail.createAt.as("orderDetailCreatedAt"),
						orderDetail.orderStatus.name.as("orderDetailOrderStatusName"),
						orderDetail.wrapping.paper.as("orderDetailWrappingPaper"),
						orderDetail.product.productName.as("orderDetailProductName")
					),
					order.sender.as("sender"),
					order.receiverContactNumber.as("receiverContactNumber"),
					order.senderContactNumber.as("senderContactNumber")
				)
			)
			.from(order)
			.join(order.user).on(order.user.id.eq(user.id))
			.leftJoin(order.details, orderDetail)
			.where(order.user.loginId.eq(request.getLoginId()))
			.orderBy(orderBy.toArray(OrderSpecifier[]::new))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.distinct()
			.fetch();

		List<ReadOrderProjectionResponse> content = results;
		long total = results.size();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<ReadOrderProjectionResponse> findAll(ReadOrderRequest request, Pageable pageable) {
		String[] sortBy = request.getSortBy();
		Boolean[] sortDesc = request.getSortDesc();
		List<OrderSpecifier> orderBy = new LinkedList<>();
		searchOrderMultiSortFilter(sortBy, sortDesc, orderBy);

		List<ReadOrderProjectionResponse> results = jpaQueryFactory
			.select(
				Projections.constructor(
					ReadOrderProjectionResponse.class,
					order.id.as("id"),
					order.orderStr.as("orderStr"),
					order.user.loginId.as("loginId"),
					order.price.as("price"),
					order.request.as("request"),
					order.address.as("address"),
					order.addressDetail.as("addressDetail"),
					order.zipcode.as("zipcode"),
					order.desiredDeliveryDate.as("desiredDeliveryDate"),
					order.receiver.as("receiver"),
					Projections.fields(
						ReadOrderDetailProjectionResponse.class,
						orderDetail.id.as("orderDetailId"),
						orderDetail.price.as("orderDetailPrice"),
						orderDetail.quantity.as("orderDetailQuantity"),
						ExpressionUtils.as(
							Expressions.stringTemplate(
								"CASE WHEN {0} = true THEN 'true' ELSE 'false' END",
								orderDetail.wrap
							),
							"orderDetailWrap"
						),
						orderDetail.createAt.as("orderDetailCreatedAt"),
						orderDetail.orderStatus.name.as("orderDetailOrderStatusName"),
						orderDetail.wrapping.paper.as("orderDetailWrappingPaper"),
						orderDetail.product.productName.as("orderDetailProductName")
					),
					order.sender.as("sender"),
					order.receiverContactNumber.as("receiverContactNumber"),
					order.senderContactNumber.as("senderContactNumber")
				)
			)
			.from(order)
			.join(order.user).on(order.user.id.eq(user.id))
			.leftJoin(order.details, orderDetail)
			.orderBy(orderBy.toArray(OrderSpecifier[]::new))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.distinct()
			.fetch();

		List<ReadOrderProjectionResponse> content = results;
		long total = results.size();

		return new PageImpl<>(content, pageable, total);
	}

	private static void searchOrderMultiSortFilter(String[] sortBy, Boolean[] sortDesc, List<OrderSpecifier> orderBy) {
		for (int i = 0; i < sortBy.length; i++) {
			String key = sortBy[i];
			Boolean value = sortDesc[i];

			switch (key) {
				case "price" -> FunctionUtil.orderDescFilter(orderBy, value, order.price, "price");
				case "loginId" -> FunctionUtil.orderDescFilter(orderBy, value, order.user.loginId, "loginId");
				case "address" -> FunctionUtil.orderDescFilter(orderBy, value, order.address, "address");
				case "orderStr" -> FunctionUtil.orderDescFilter(orderBy, value, order.orderStr, "orderStr");
				case "desiredDeliveryDate" ->
					FunctionUtil.orderDescFilter(orderBy, value, order.desiredDeliveryDate, "desiredDeliveryDate");
				default -> {
				}
			}
		}
	}
}
