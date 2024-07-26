package store.buzzbook.core.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.dto.product.ProductDetailResponse;
import store.buzzbook.core.service.product.ProductDetailService;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "상품 상세 조회", description = "상품정보, 연결된 책정보, 리뷰 최근 5개")
public class ProductDetailController {

	private final ProductDetailService productDetailService;

	@GetMapping("/{id}/detail")
	public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable int id){
		return ResponseEntity.ok(productDetailService.getProductDetail(id));
	}
}
