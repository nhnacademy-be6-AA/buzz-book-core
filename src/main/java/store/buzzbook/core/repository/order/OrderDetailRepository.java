package store.buzzbook.core.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import store.buzzbook.core.entity.order.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	List<OrderDetail> findAllById(long orderId);
	List<OrderDetail> findAllByOrder_IdAndOrder_User_LoginId(long orderId, String loginId);
	List<OrderDetail> findAllByOrder_IdAndOrder_OrderPassword(long orderId, String orderPassword);
	List<OrderDetail> findAllByOrder_IdAndOrderStatus_Id(long orderId, long orderStatusId);
	List<OrderDetail> findAllByOrder_Id(long orderId);
	List<OrderDetail> findAllByOrder_OrderStr(String orderStr);
	OrderDetail findByIdAndOrder_User_LoginId(long orderDetailId, String loginId);
}
