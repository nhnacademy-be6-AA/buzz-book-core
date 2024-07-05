package store.buzzbook.core.mapper.order;

import java.time.LocalDateTime;

import store.buzzbook.core.dto.order.CreateOrderDetailRequest;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.entity.product.Product;

public class OrderDetailMapper {

	public static ReadOrderDetailResponse toDto(OrderDetail orderDetail, ProductResponse productResponse, ReadWrappingResponse readWrappingResponse) {
		return ReadOrderDetailResponse.builder()
			.id(orderDetail.getId())
			.price(orderDetail.getPrice())
			.wrap(orderDetail.isWrap())
			.createdAt(orderDetail.getCreateAt())
			.readOrderStatusResponse(OrderStatusMapper.toDto(orderDetail.getOrderStatus()))
			.quantity(orderDetail.getQuantity())
			.productResponse(productResponse)
			.readWrappingResponse(readWrappingResponse)
			.couponCode(orderDetail.getCouponCode())
			.updateAt(orderDetail.getUpdateAt())
			.build();
	}

	public static OrderDetail toEntity(CreateOrderDetailRequest createOrderDetailRequest, Order order,
		Wrapping wrapping, Product product, OrderStatus orderStatus) {

		return OrderDetail.builder()
			.orderStatus(orderStatus)
			.order(order)
			.price(createOrderDetailRequest.getPrice())
			.wrap(createOrderDetailRequest.isWrap())
			.wrapping(wrapping)
			.product(product)
			.quantity(createOrderDetailRequest.getQuantity())
			.order(order)
			.createAt(LocalDateTime.now())
			.couponCode(createOrderDetailRequest.getCouponCode())
			.updateAt(LocalDateTime.now())
			.build();
	}
}
