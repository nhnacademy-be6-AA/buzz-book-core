package store.buzzbook.core.mapper.order;

import store.buzzbook.core.dto.order.CreateOrderDetailRequest;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.entity.product.Product;

public class OrderDetailMapper {
	public static ReadOrderDetailResponse toDto(OrderDetail orderDetail) {
		return ReadOrderDetailResponse.builder()
			.id(orderDetail.getId())
			.price(orderDetail.getPrice())
			.wrap(orderDetail.isWrap())
			.createdDate(orderDetail.getCreateDate())
			.orderStatus(OrderStatusMapper.toDto(orderDetail.getOrderStatus()))
			.quantity(orderDetail.getQuantity())
			.product(orderDetail.getProduct())
			.wrapping(WrappingMapper.toDto(orderDetail.getWrapping()))
			// .order(orderDetail.getOrder())
			.build();
	}

	public static OrderDetail toEntity(CreateOrderDetailRequest createOrderDetailRequest, Order order,
		Wrapping wrapping, Product product, OrderStatus orderStatus) {
		return OrderDetail.builder()
			.orderStatus(orderStatus)
			.order(order)
			.price(createOrderDetailRequest.getPrice())
			.createDate(createOrderDetailRequest.getCreateDate())
			.wrap(createOrderDetailRequest.isWrap())
			.wrapping(wrapping)
			.product(product)
			.quantity(createOrderDetailRequest.getQuantity())
			.build();
	}

	public static OrderDetail toEntity(UpdateOrderDetailRequest updateOrderDetailRequest, OrderStatus orderStatus) {
		return OrderDetail.builder()
			.id(updateOrderDetailRequest.getId())
			.orderStatus(orderStatus)
			.build();
	}
}
