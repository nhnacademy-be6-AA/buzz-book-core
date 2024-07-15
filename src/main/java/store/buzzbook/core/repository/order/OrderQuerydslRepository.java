package store.buzzbook.core.repository.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrdersRequest;

public interface OrderQuerydslRepository {
	List<ReadOrderProjectionResponse> findAllByUser_LoginId(ReadOrdersRequest request, String loginId);
	List<ReadOrderProjectionResponse> findAll(ReadOrdersRequest request);
}
