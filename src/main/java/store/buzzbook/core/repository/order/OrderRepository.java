package store.buzzbook.core.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderQuerydslRepository {
	Order findByOrderStr(String orderStr);
}
