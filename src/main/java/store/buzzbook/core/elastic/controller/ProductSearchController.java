package store.buzzbook.core.elastic.controller;

import java.util.List;

import org.springframework.data.domain.Page;
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
import store.buzzbook.core.elastic.document.BookDocument;
import store.buzzbook.core.elastic.service.DataTransferService;
import store.buzzbook.core.elastic.service.ElasticsearchService;

@RestController
@RequestMapping("/api/product-search")
@RequiredArgsConstructor
@Tag(name = "엘라스틱 서치 상품 검색", description = "상품 검색 기능")
public class ProductSearchController {

	private final ElasticsearchService elasticsearchService;
	private final DataTransferService dataTransferService;


	@GetMapping("/search")
	@Operation(summary = "상품 검색", description = "상품명을 기준으로 검색")
	@ApiResponse(responseCode = "200", description = "검색 성공시 Page<BookDocument> 반환")
	public ResponseEntity<Page<BookDocument>> searchProducts(
		@RequestParam @Parameter(description = "검색할 상품명", required = true) String query,
		@RequestParam(required = false, defaultValue = "1") @Parameter(description = "페이지 번호") Integer pageNo,
		@RequestParam(required = false, defaultValue = "10") @Parameter(description = "한 페이지에 보여질 아이템 수") Integer pageSize) throws JsonProcessingException {

		Page<BookDocument> response = elasticsearchService.searchProducts(query, pageNo, pageSize);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/datainit")
	@Operation(summary = "데이터 싱크", description = "MySQL의 데이터를 Elasticsearch로")
	@ApiResponse(responseCode = "200", description = "성공시 데이터 총 갯수 반환")
	public ResponseEntity<Long> searchProducts(){
		return ResponseEntity.ok(dataTransferService.mySqlDataTransferToElastic());
	}

	// 새로운 자동 완성 엔드포인트
	// @GetMapping("/autocomplete")
	// @Operation(summary = "자동 완성", description = "입력된 단어에 대한 자동 완성 제안")
	// @ApiResponse(responseCode = "200", description = "성공시 자동 완성 제안 반환")
	// public ResponseEntity<List<String>> autocomplete(
	// 	@RequestParam @Parameter(description = "자동 완성 검색어", required = true) String query) {
	// 	List<String> suggestions = elasticsearchService.getAutocompleteSuggestions(query);
	// 	return ResponseEntity.ok(suggestions);
	// }
}
