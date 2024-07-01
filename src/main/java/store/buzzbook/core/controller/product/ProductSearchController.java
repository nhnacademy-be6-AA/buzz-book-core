package store.buzzbook.core.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.service.product.ElasticsearchService;

@RestController
@RequestMapping("/api/product-search")
@RequiredArgsConstructor
@Tag(name = "엘라스틱 서치 상품 검색", description = "상품 검색 기능")
public class ProductSearchController {

	private final ElasticsearchService elasticsearchService;

	@GetMapping("/search")
	@Operation(summary = "상품 검색", description = "상품명을 기준으로 검색")
	@ApiResponse(responseCode = "200", description = "검색 성공시 Elasticsearch 검색 결과 반환")
	public ResponseEntity<String> searchProducts(
		@RequestParam @Parameter(description = "검색할 상품명", required = true) String query) {
		String response = elasticsearchService.searchProducts(query);
		return ResponseEntity.ok(response);
	}
}