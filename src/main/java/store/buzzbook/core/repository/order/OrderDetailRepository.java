package store.buzzbook.core.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	List<OrderDetail> findAllByOrder_Id(Long orderId);
	List<OrderDetail> findAllByOrder_IdAndOrderStatus(Long orderId, OrderStatus orderStatus);

}
