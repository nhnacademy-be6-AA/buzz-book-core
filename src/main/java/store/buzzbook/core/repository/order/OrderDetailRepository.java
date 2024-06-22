package store.buzzbook.core.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import store.buzzbook.core.entity.order.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	List<OrderDetail> findAllById(Long orderId);
	List<OrderDetail> findAllByOrder_IdAndOrder_User_LoginId(Long orderId, String loginId);
	List<OrderDetail> findAllByOrder_IdAndOrderStatus_Id(long orderId, long orderStatusId);
	List<OrderDetail> findAllByOrder_Id(long orderId);
}
