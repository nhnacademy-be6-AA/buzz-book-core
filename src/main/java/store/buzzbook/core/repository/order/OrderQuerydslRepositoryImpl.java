package store.buzzbook.core.repository.order;

import static store.buzzbook.core.entity.order.QOrder.*;
import static store.buzzbook.core.entity.order.QOrderDetail.*;
import static store.buzzbook.core.entity.user.QUser.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrdersRequest;

@RequiredArgsConstructor
public class OrderQuerydslRepositoryImpl implements OrderQuerydslRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<ReadOrderProjectionResponse> findAllByUser_LoginId(ReadOrdersRequest request, Pageable pageable) {

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
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.distinct()
			.fetch();

		List<ReadOrderProjectionResponse> content = results;
		long total = results.size();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<ReadOrderProjectionResponse> findAll(ReadOrdersRequest request, Pageable pageable) {

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
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.distinct()
			.fetch();

		List<ReadOrderProjectionResponse> content = results;
		long total = results.size();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public List<ReadOrderProjectionResponse> findByUser_LoginIdAndOrderStr(String loginId, String orderStr) {
		return jpaQueryFactory
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
			.where(order.orderStr.eq(orderStr), order.user.loginId.eq(loginId))
			.fetch();
	}
}
