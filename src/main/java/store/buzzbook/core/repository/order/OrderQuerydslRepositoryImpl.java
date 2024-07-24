package store.buzzbook.core.repository.order;

import static com.querydsl.core.group.GroupBy.*;
import static store.buzzbook.core.entity.order.QOrder.*;
import static store.buzzbook.core.entity.order.QOrderDetail.*;
import static store.buzzbook.core.entity.user.QUser.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrdersRequest;
import store.buzzbook.core.dto.order.ReadOrdersResponse;

@RequiredArgsConstructor
public class OrderQuerydslRepositoryImpl implements OrderQuerydslRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<ReadOrderProjectionResponse> findAllByUser_LoginId(ReadOrdersRequest request, String loginId) {

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
						orderDetail.product.productName.as("orderDetailProductName"),
						orderDetail.updateAt.as("orderDetailUpdatedAt")
					),
					order.sender.as("sender"),
					order.receiverContactNumber.as("receiverContactNumber"),
					order.senderContactNumber.as("senderContactNumber"),
					order.couponCode.as("couponCode"),
					order.deliveryRate.as("deliveryRate"),
					order.orderEmail.as("orderEmail")
				)
			)
			.from(order)
			.join(order.user).on(order.user.id.eq(user.id))
			.leftJoin(order.details, orderDetail)
			.where(order.user.loginId.eq(loginId))
			.fetch();
	}

	@Override
	public List<ReadOrderProjectionResponse> findAll(ReadOrdersRequest request) {

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
						orderDetail.product.productName.as("orderDetailProductName"),
						orderDetail.updateAt.as("orderDetailUpdatedAt")
					),
					order.sender.as("sender"),
					order.receiverContactNumber.as("receiverContactNumber"),
					order.senderContactNumber.as("senderContactNumber"),
					order.couponCode.as("couponCode"),
					order.deliveryRate.as("deliveryRate"),
					order.orderEmail.as("orderEmail")
				)
			)
			.from(order)
			.join(order.user).on(order.user.id.eq(user.id))
			.leftJoin(order.details, orderDetail)
			.fetch();
	}

	@Override
	public Slice<ReadOrdersResponse> findAll2(ReadOrdersRequest request, Pageable pageable) {
		List<ReadOrdersResponse> results = jpaQueryFactory
			.select(order)
			.from(order)
			.join(order.user).on(order.user.id.eq(user.id))
			.leftJoin(order.details, orderDetail)
			.orderBy(order.deliveryRate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.transform(GroupBy.groupBy(order.orderStr).list(Projections.constructor(
				ReadOrdersResponse.class,
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
				GroupBy.list(Projections.fields(
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
					orderDetail.product.productName.as("orderDetailProductName"),
					orderDetail.updateAt.as("orderDetailUpdatedAt")
				)),
				order.sender.as("sender"),
				order.receiverContactNumber.as("receiverContactNumber"),
				order.senderContactNumber.as("senderContactNumber"),
				order.couponCode.as("couponCode"),
				order.deliveryRate.as("deliveryRate"),
				order.orderEmail.as("orderEmail")
			)));

		long total = results.size();

		return new PageImpl<>(results, pageable, total);
	}
}
