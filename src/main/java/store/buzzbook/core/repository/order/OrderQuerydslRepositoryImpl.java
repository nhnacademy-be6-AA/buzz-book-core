package store.buzzbook.core.repository.order;

import static store.buzzbook.core.entity.order.QOrder.*;
import static store.buzzbook.core.entity.order.QOrderDetail.*;
import static store.buzzbook.core.entity.payment.QBillLog.*;
import static store.buzzbook.core.entity.user.QUser.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.order.ReadOrderDetailProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderWithBillLogsResponse;
import store.buzzbook.core.dto.order.ReadOrdersRequest;
import store.buzzbook.core.dto.order.ReadOrdersResponse;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.dto.payment.ReadBillLogsRequest;

@RequiredArgsConstructor
public class OrderQuerydslRepositoryImpl implements OrderQuerydslRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Slice<ReadOrdersResponse> findAll(ReadOrdersRequest request, Pageable pageable) {
		List<ReadOrdersResponse> results = jpaQueryFactory
			.select(order)
			.from(order)
			.join(order.user).on(order.user.id.eq(user.id))
			.leftJoin(order.details, orderDetail)
			.orderBy(order.desiredDeliveryDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
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
				order.orderEmail.as("orderEmail"),
				order.orderStatus.name.as("orderStatus"),
				order.deductedPoints.as("deductedPoints"),
				order.earnedPoints.as("earnedPoints"),
				order.deductedCouponPrice.as("deductedCouponPrice")
			)));

		return checkEndPage(results, pageable);
	}

	private Slice<ReadOrdersResponse> checkEndPage(List<ReadOrdersResponse> orders, Pageable pageable) {
		boolean hasNext = false;
		if (orders.size() > pageable.getPageSize()) {
			hasNext = true;
			orders.remove(pageable.getPageSize());
		}

		return new SliceImpl<>(orders, pageable, hasNext);
	}

	@Override
	public Slice<ReadOrdersResponse> findAllByUser_LoginId(ReadOrdersRequest request, String loginId,
		Pageable pageable) {
		List<ReadOrdersResponse> results = jpaQueryFactory
			.select(order)
			.from(order)
			.join(order.user).on(order.user.id.eq(user.id))
			.leftJoin(order.details, orderDetail)
			.where(order.user.loginId.eq(loginId))
			.orderBy(order.desiredDeliveryDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
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
				order.orderEmail.as("orderEmail"),
				order.orderStatus.name.as("orderStatus"),
				order.deductedPoints.as("deductedPoints"),
				order.earnedPoints.as("earnedPoints"),
				order.deductedCouponPrice.as("deductedCouponPrice")
			)));

		return checkEndPage(results, pageable);
	}

	@Override
	public Slice<ReadOrderWithBillLogsResponse> readOrdersWithBillLogs(ReadBillLogsRequest request, Pageable pageable) {
		List<ReadOrderWithBillLogsResponse> results = jpaQueryFactory
			.select(order)
			.from(order)
			.leftJoin(order.user, user)
			.leftJoin(billLog).on(billLog.order.eq(order))
			.orderBy(order.desiredDeliveryDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() * 2)
			.transform(GroupBy.groupBy(order.orderStr).list(
				Projections.constructor(ReadOrderWithBillLogsResponse.class,
					order.id.as("orderId"),
					order.orderStr.as("orderStr"),
					user.loginId.as("loginId"),
					order.price.as("orderPrice"),
					order.request.as("request"),
					order.address.as("address"),
					order.addressDetail.as("addressDetail"),
					order.zipcode.as("zipcode"),
					order.desiredDeliveryDate.as("desiredDeliveryDate"),
					order.receiver.as("receiver"),
					order.sender.as("sender"),
					order.receiverContactNumber.as("receiverContactNumber"),
					order.senderContactNumber.as("senderContactNumber"),
					order.couponCode.as("couponCode"),
					order.orderEmail.as("orderEmail"),
					order.deliveryRate.as("deliveryRate"),
					order.orderStatus.name.as("orderStatus"),
					order.deductedPoints.as("deductedPoints"),
					order.earnedPoints.as("earnedPoints"),
					order.deductedCouponPrice.as("deductedCouponPrice"),
					GroupBy.list(Projections.fields(
						ReadBillLogWithoutOrderResponse.class,
						billLog.id.as("id"),
						billLog.payment.as("payment"),
						billLog.price.as("price"),
						billLog.payAt.as("payAt"),
						billLog.status.as("status"),
						billLog.paymentKey.as("paymentKey"),
						billLog.cancelReason.as("cancelReason"))
					)
				)));

		boolean hasNext = results.size() > pageable.getPageSize();

		if (hasNext) {
			results.subList(0, pageable.getPageSize());
		}

		return new SliceImpl<>(results, pageable, hasNext);
	}
}
