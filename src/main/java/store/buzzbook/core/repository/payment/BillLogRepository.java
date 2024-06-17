package store.buzzbook.core.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.payment.BillLog;

public interface BillLogRepository extends JpaRepository<BillLog, Long> {
}
