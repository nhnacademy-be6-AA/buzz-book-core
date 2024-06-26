package store.buzzbook.core.mapper.order;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import store.buzzbook.core.common.util.ZonedDateTimeParser;
import store.buzzbook.core.dto.order.CreateOrderDetailRequest;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.entity.product.Product;

public class OrderDetailMapper {
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


	public static ReadOrderDetailResponse toDto(OrderDetail orderDetail) {
		return ReadOrderDetailResponse.builder()
			.id(orderDetail.getId())
			.price(orderDetail.getPrice())
			.wrap(orderDetail.isWrap())
			.createdAt(orderDetail.getCreateAt())
			.orderStatus(OrderStatusMapper.toDto(orderDetail.getOrderStatus()))
			.quantity(orderDetail.getQuantity())
			.product(orderDetail.getProduct())
			.wrapping(WrappingMapper.toDto(orderDetail.getWrapping()))
			// .readOrderResponse(readOrderResponse)
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
			.build();
	}
}
