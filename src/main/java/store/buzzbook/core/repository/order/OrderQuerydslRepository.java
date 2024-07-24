package store.buzzbook.core.repository.order;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import store.buzzbook.core.dto.order.ReadOrderWithBillLogsResponse;
import store.buzzbook.core.dto.order.ReadOrdersRequest;
import store.buzzbook.core.dto.order.ReadOrdersResponse;
import store.buzzbook.core.dto.payment.ReadBillLogsRequest;

public interface OrderQuerydslRepository {
	Slice<ReadOrdersResponse> findAll(ReadOrdersRequest request, Pageable pageable);
	Slice<ReadOrdersResponse> findAllByUser_LoginId(ReadOrdersRequest request, String loginId, Pageable pageable);
	Slice<ReadOrderWithBillLogsResponse> readOrdersWithBillLogs(ReadBillLogsRequest request, Pageable pageable);
}
