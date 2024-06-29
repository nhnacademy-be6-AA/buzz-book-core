package store.buzzbook.core.repository.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import store.buzzbook.core.dto.payment.ReadBillLogProjectionResponse;
import store.buzzbook.core.dto.payment.ReadBillLogRequest;

public interface BillLogQuerydslRepository {
	Page<ReadBillLogProjectionResponse> findAll(ReadBillLogRequest request, Pageable pageable);
}
