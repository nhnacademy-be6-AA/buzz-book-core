package store.buzzbook.core.controller.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.service.product.ProductTagService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productTags")
@RequiredArgsConstructor
@Tag(name = "상품태그 관리", description = "태그 CRD")
public class ProductTagController {

	private final ProductTagService productTagService;

	@GetMapping("/{productId}")
	@Operation(summary = "특정 상품에 대한 태그 리스트 조회")
	public ResponseEntity<List<String>> getTagsByProductId(@PathVariable int productId) {
		List<String> tags = productTagService.getTagsByProductId(productId);
		return ResponseEntity.ok(tags);
	}

	@PostMapping("/{productId}/tags")
	@Operation(summary = "특정 상품에 대한 태그 추가")
	public ResponseEntity<Void> addTagToProduct(@PathVariable int productId, @RequestBody List<Integer> tagIds) {
		productTagService.addTagToProduct(productId, tagIds);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{productId}/tags")
	@Operation(summary = "특정 상품에 대한 태그 삭제")
	public ResponseEntity<Void> removeTagFromProduct(@PathVariable int productId, @RequestBody List<Integer> tagIds) {
		productTagService.removeAllTagsFromProduct(productId, tagIds);
		return ResponseEntity.noContent().build();
	}
}
