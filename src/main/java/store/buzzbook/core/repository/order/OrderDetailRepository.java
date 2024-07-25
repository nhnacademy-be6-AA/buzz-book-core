package store.buzzbook.core.repository.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import store.buzzbook.core.entity.order.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	List<OrderDetail> findAllByOrder_IdAndOrder_User_LoginId(long orderId, String loginId);
	List<OrderDetail> findAllByOrder_IdAndOrder_OrderEmail(long orderId, String orderEmail);

	@Query("SELECT od FROM OrderDetail od WHERE od.order.id = :orderId")
	List<OrderDetail> findAllByOrder_Id(long orderId);

	OrderDetail findByIdAndOrder_User_LoginId(long orderDetailId, String loginId);

	@Query("select o.orderStr from OrderDetail od inner join od.order o where od.id = :orderDetailId")
	String findOrderStrByOrderDetailId(@Param("orderDetailId") Long orderDetailId);

	@Query("SELECT od FROM OrderDetail od " +
		"WHERE od.order.user.id = :userId " +
		"AND od.orderStatus.name IN :orderStatusList " +
		"AND NOT EXISTS (SELECT 1 FROM Review r WHERE r.orderDetail.id = od.id) " +
		"ORDER BY od.createAt DESC")
	Page<OrderDetail> findAllNoExistReviewOrderDetailsByUserId(
		long userId, List<String> orderStatusList, Pageable pageable);

}
