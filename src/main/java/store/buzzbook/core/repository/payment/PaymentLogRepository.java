package store.buzzbook.core.repository.payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.payment.PaymentLog;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Integer> {
	// List<PaymentLog> findByOrder_OrderStrAndOrder_User_LoginId(String orderStr, String loginId);
	// List<PaymentLog> findByOrder_OrderStr(String orderStr);
}
