package store.buzzbook.core.repository.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.entity.order.Order;

public interface OrderQuerydslRepository {
	Page<Order> findAllByUser_LoginId(ReadOrderRequest request, Pageable pageable);
	Page<Order> findAll(ReadOrderRequest request, Pageable pageable);
}
