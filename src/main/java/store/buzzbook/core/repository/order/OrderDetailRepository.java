package store.buzzbook.core.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.order.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	List<OrderDetail> findAllByOrder_Id(Long orderId);
}
