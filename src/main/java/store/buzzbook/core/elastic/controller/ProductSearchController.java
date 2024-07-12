package store.buzzbook.core.elastic.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/product-search")
@RequiredArgsConstructor
@Tag(name = "엘라스틱 서치 상품 검색", description = "상품 검색 기능")
public class ProductSearchController {

	private final ElasticsearchService elasticsearchService;
	private final ElasticDataTransferService dataTransferService;

	@GetMapping("/search")
	@Operation(summary = "상품 검색", description = "상품명을 기준으로 검색")
	@ApiResponse(responseCode = "200", description = "검색 성공시 Elasticsearch 검색 결과 반환")
	public ResponseEntity<List<ProductDocument>> searchProducts(
		@RequestParam @Parameter(description = "검색할 상품명", required = true) String query) throws
		JsonProcessingException {
		List<ProductDocument> response = elasticsearchService.searchProducts(query);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/datainit")
	@Operation(summary = "데이터 싱크", description = "MySQL의 데이터를 Elasticsearch로")
	@ApiResponse(responseCode = "200", description = "성공시 데이터 총 갯수 반환")
	public ResponseEntity<Long> searchProducts(){
		return ResponseEntity.ok(dataTransferService.mySqlDataTransferToElastic());
	}
}
