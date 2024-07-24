package store.buzzbook.core.repository.order;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrdersRequest;
import store.buzzbook.core.dto.order.ReadOrdersResponse;

public interface OrderQuerydslRepository {
	List<ReadOrderProjectionResponse> findAllByUser_LoginId(ReadOrdersRequest request, String loginId);
	List<ReadOrderProjectionResponse> findAll(ReadOrdersRequest request);
	Slice<ReadOrdersResponse> findAll2(ReadOrdersRequest request, Pageable pageable);
}
