package store.buzzbook.core.repository.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderResponse;

public interface OrderQuerydslRepository {
	Page<ReadOrderResponse> findAllByUser_LoginId(ReadOrderRequest request, Pageable pageable);
	Page<ReadOrderResponse> findAll(ReadOrderRequest request, Pageable pageable);
}
