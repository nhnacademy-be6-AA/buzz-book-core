package store.buzzbook.core.controller.product;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.service.product.ProductTagService;

@RestController
@RequestMapping("/api/product-tags")
@RequiredArgsConstructor
@Tag(name = "상품의 태그 관리", description = "태그 C__D")
public class ProductTagController {

	private final ProductTagService productTagService;

	@PostMapping("/{productId}/tags")
	public ResponseEntity<Void> addTagToProduct(@PathVariable int productId, @RequestBody List<Integer> tagIds) {
		productTagService.addTagToProduct(productId, tagIds);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{productId}/tags")
	public ResponseEntity<Void> removeTagFromProduct(@PathVariable int productId, @RequestBody List<Integer> tagIds) {
		productTagService.removeAllTagsFromProduct(productId, tagIds);
		return ResponseEntity.noContent().build();
	}
}
