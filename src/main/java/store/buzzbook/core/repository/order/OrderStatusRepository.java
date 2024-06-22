package store.buzzbook.core.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.order.OrderStatus;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
	OrderStatus findByName(String name);
}
