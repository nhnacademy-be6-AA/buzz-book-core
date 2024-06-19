package store.buzzbook.core.mapper.order;

import store.buzzbook.core.dto.order.OrderDetailResponse;
import store.buzzbook.core.entity.order.OrderDetail;

public class OrderDetailMapper {
	public static OrderDetailResponse toDto(OrderDetail orderDetail) {
		return OrderDetailResponse.builder()
			.id(orderDetail.getId())
			.price(orderDetail.getPrice())
			.wrap(orderDetail.isWrap())
			.createdDate(orderDetail.getCreateDate())
			.orderStatus(orderDetail.getOrderStatus())
			.quantity(orderDetail.getQuantity())
			.product(orderDetail.getProduct())
			.wrapping(orderDetail.getWrapping())
			.order(orderDetail.getOrder())
			.build();
	}
}
