package store.buzzbook.core.repository.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrdersRequest;

public interface OrderQuerydslRepository {
	Page<ReadOrderProjectionResponse> findAllByUser_LoginId(ReadOrdersRequest request, Pageable pageable);
	Page<ReadOrderProjectionResponse> findAll(ReadOrdersRequest request, Pageable pageable);
	List<ReadOrderProjectionResponse> findByUser_LoginIdAndOrderStr(String loginId, String orderStr);
}
