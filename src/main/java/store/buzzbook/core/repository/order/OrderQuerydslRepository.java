package store.buzzbook.core.repository.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderResponse;

public interface OrderQuerydslRepository {
	Page<ReadOrderProjectionResponse> findAllByUser_LoginId(ReadOrderRequest request, Pageable pageable);
	Page<ReadOrderProjectionResponse> findAll(ReadOrderRequest request, Pageable pageable);
}
