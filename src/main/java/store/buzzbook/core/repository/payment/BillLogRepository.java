package store.buzzbook.core.repository.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import store.buzzbook.core.entity.payment.BillLog;

public interface BillLogRepository extends JpaRepository<BillLog, Long> {
	BillLog findByOrder_id(Long id);

	@Query("select b from BillLog b join Order o on b.order.id = o.id where o.user.id = :userId")
	Page<BillLog> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}
