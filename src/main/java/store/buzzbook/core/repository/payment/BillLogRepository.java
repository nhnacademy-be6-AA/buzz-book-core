package store.buzzbook.core.repository.payment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import store.buzzbook.core.entity.payment.BillLog;

public interface BillLogRepository extends JpaRepository<BillLog, Long> {
	@Query("select b from BillLog b join Order o on b.order.id = o.id where o.user.id = :userId and b.order.orderStr = :orderStr order by b.payAt desc")
	List<BillLog> findByUserIdAndOrderStr(@Param("userId") long userId, @Param("orderStr") String orderStr);

	List<BillLog> findByOrder_OrderStr(String orderStr);

	List<BillLog> findByOrder_OrderStrAndOrder_User_Id(String orderStr, long userId);
	List<BillLog> findByOrder_OrderStrAndOrder_OrderEmail(String orderStr, String orderEmail);

	boolean existsByPaymentAndPaymentKey(String payment, String paymentKey);
}
