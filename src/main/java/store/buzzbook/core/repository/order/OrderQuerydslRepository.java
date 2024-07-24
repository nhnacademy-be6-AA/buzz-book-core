package store.buzzbook.core.repository.order;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import store.buzzbook.core.dto.order.ReadOrdersRequest;
import store.buzzbook.core.dto.order.ReadOrdersResponse;

public interface OrderQuerydslRepository {
	Slice<ReadOrdersResponse> findAll(ReadOrdersRequest request, Pageable pageable);
	Slice<ReadOrdersResponse> findAllByUser_LoginId(ReadOrdersRequest request, String loginId, Pageable pageable);
}
