package store.buzzbook.core.service.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.order.OrderDetailResponse;
import store.buzzbook.core.dto.order.OrderReadResponse;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.mapper.order.OrderDetailMapper;
import store.buzzbook.core.mapper.order.OrderMapper;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final UserRepository userRepository;

	public Page<OrderReadResponse> readMyOrders(long userId, Pageable pageable) {
		userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));

		Page<Order> orders = orderRepository.findByUser_Id(userId, pageable);
		List<OrderReadResponse> responses = new ArrayList<>();

		for (Order order : orders) {
			List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
			List<OrderDetailResponse> details = new ArrayList<>();

			for (OrderDetail orderDetail : orderDetails) {
				details.add(OrderDetailMapper.toDto(orderDetail));
			}

			responses.add(OrderMapper.toDto(order, details));
		}

		return new PageImpl<>(responses, pageable, orders.getTotalElements());
	}

}
