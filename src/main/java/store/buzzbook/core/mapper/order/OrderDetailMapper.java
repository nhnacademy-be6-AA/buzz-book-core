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
	private static DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");


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
			// .readOrderResponse(readOrderResponse)
			.build();
	}

	public static OrderDetail toEntity(CreateOrderDetailRequest createOrderDetailRequest, Order order,
		Wrapping wrapping, Product product, OrderStatus orderStatus) {
		LocalDateTime localDateTime = LocalDateTime.parse(createOrderDetailRequest.getCreateDate(), inputFormatter);
		ZoneId zid = ZoneId.of("Asia/Seoul");
		ZonedDateTime zdt = localDateTime.atZone(zid);

		return OrderDetail.builder()
			.orderStatus(orderStatus)
			.order(order)
			.price(createOrderDetailRequest.getPrice())
			.wrap(createOrderDetailRequest.isWrap())
			.wrapping(wrapping)
			.product(product)
			.quantity(createOrderDetailRequest.getQuantity())
			.order(order)
			.createDate(ZonedDateTime.now())
			.build();
	}
}
