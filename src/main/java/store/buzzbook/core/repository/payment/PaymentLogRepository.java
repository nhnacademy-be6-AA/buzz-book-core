package store.buzzbook.core.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.payment.PaymentLog;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
}
