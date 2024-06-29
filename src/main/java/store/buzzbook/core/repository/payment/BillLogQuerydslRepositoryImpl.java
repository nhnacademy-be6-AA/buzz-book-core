package store.buzzbook.core.repository.payment;

import static store.buzzbook.core.entity.order.QOrder.*;
import static store.buzzbook.core.entity.payment.QBillLog.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.order.ReadOrderWithoutDetailsProjectionResponse;
import store.buzzbook.core.dto.payment.ReadBillLogProjectionResponse;
import store.buzzbook.core.dto.payment.ReadBillLogsRequest;

@RequiredArgsConstructor
public class BillLogQuerydslRepositoryImpl implements BillLogQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ReadBillLogProjectionResponse> findAll(ReadBillLogsRequest request, Pageable pageable) {
		List<ReadBillLogProjectionResponse> results = queryFactory
			.select(
				Projections.constructor(
					ReadBillLogProjectionResponse.class,
					billLog.id.as("id"),
					billLog.payment.as("payment"),
					billLog.price.as("price"),
					billLog.payAt.as("payAt"),
					billLog.status.as("status"),
					billLog.paymentKey.as("paymentKey"),
					Projections.fields(
						ReadOrderWithoutDetailsProjectionResponse.class,
						order.id.as("orderId"),
						order.orderStr.as("orderStr"),
						order.user.loginId.as("loginId"),
						order.price.as("orderPrice"),
						order.request.as("request"),
						order.address.as("address"),
						order.addressDetail.as("addressDetail"),
						order.zipcode.as("zipcode"),
						order.desiredDeliveryDate.as("desiredDeliveryDate"),
						order.receiver.as("receiver"),
						order.sender.as("sender"),
						order.receiverContactNumber.as("receiverContactNumber"),
						order.senderContactNumber.as("senderContactNumber")
					),
					billLog.cancelReason.as("cancelReason")
				)
			).from(billLog)
			.leftJoin(billLog.order, order)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.distinct()
			.fetch();

		List<ReadBillLogProjectionResponse> content = results;
		long total = results.size();

		return new PageImpl<>(content, pageable, total);
	}
}
