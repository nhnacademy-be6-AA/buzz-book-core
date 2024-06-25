package store.buzzbook.core.repository.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import store.buzzbook.core.entity.payment.BillLog;

public interface BillLogRepository extends JpaRepository<BillLog, Long> {
	@Query("select b from BillLog b join Order o on b.order.id = o.id where o.user.id = :userId and b.order.id = :orderId")
	BillLog findByUserIdAndId(@Param("userId") long userId, @Param("orderId") String orderId);

	@Query("select b from BillLog b join Order o on b.order.id = o.id where o.user.loginId = :loginId")
	Page<BillLog> findAllByLoginId(@Param("loginId") String loginId, Pageable pageable);
}
