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
@Tag(name = "상품태그 관리", description = "태그 CRUD")
public class ProductTagController {

	private final ProductTagService productTagService;

	@GetMapping("/{productId}")
	@Operation(summary = "특정 상품에 대한 태그 조회")
	public ResponseEntity<List<String>> getTagsByProductId(@PathVariable int productId) {
		List<String> tags = productTagService.getTagsByProductId(productId);
		return ResponseEntity.ok(tags);
	}


	@PostMapping("/{productId}/tags/{tagId}")
	@Operation(summary = "특정 상품에 대한 태그 추가")
	public ResponseEntity<Void> addTagToProduct(@PathVariable int productId, @PathVariable int tagId) {
		productTagService.addTagToProduct(productId, tagId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{productId}/tags/{tagId}")
	@Operation(summary = "특정 상품에서 태그 삭제")
	public ResponseEntity<Void> removeTagFromProduct(@PathVariable int productId, @PathVariable int tagId) {
		productTagService.removeTagFromProduct(productId, tagId);
		return ResponseEntity.ok().build();
	}
}