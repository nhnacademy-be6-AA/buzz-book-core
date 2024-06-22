package store.buzzbook.core.repository.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	Page<Order> findAllByUser_Id(long userId, Pageable pageable);
	Page<Order> findAllByUser_LoginId(String loginId, Pageable pageable);
}
