package store.buzzbook.core.controller.product;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.product.ProductRequest;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.product.ProductUpdateRequest;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.service.product.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "상품 관리", description = "상품 CRUD")
public class ProductController {

	private final ProductService productService;


	@PostMapping
	@Operation(summary = "상품 추가", description = "새로운 상품 추가.<br>요청 본문에 ProductRequest DTO 사용.")
	@ApiResponse(responseCode = "200", description = "상품 추가 성공시 추가된 상품의 ProductResponse 반환")

	public ResponseEntity<ProductResponse> createProduct(
		@RequestBody @Parameter(description = "추가할 상품의 정보(ProductRequest)", required = true)
		ProductRequest productReq) {
		ProductResponse saveProduct = productService.saveProduct(productReq);
		return ResponseEntity.ok(saveProduct);
	}


	@GetMapping
	@Operation(summary = "조건으로 상품 목록 조회", description = "상품을 id 순서로 조회")
	@ApiResponse(responseCode = "200", description = "조회 성공시 page<productResponse> 반환")

	public ResponseEntity<Page<ProductResponse>> getAllProduct(@RequestParam(required = false) Product.StockStatus status,
		@RequestParam(required = false, defaultValue = "0") @Parameter(description = "페이지 번호")
		Integer pageNo,
		@RequestParam(required = false, defaultValue = "10") @Parameter(description = "한 페이지에 보여질 아이템 수")
		Integer pageSize) {
		if (status == null) {
			return ResponseEntity.ok(productService.getAllProducts(pageNo, pageSize));
		}
		return ResponseEntity.ok(productService.getAllProductsByStockStatus(status, pageNo, pageSize));
	}


	@GetMapping("/{id}")
	@Operation(summary = "상품 조회", description = "주어진 id(int)에 해당하는 상품 정보 조회")
	@ApiResponse(responseCode = "200", description = "상품 조회 성공시 ProductResponse 반환")

	public ResponseEntity<ProductResponse> getProductById(@PathVariable int id) {
		ProductResponse product = productService.getProductById(id);
		return ResponseEntity.ok(product);
	}

	@PutMapping("/{id}")
	@Operation(summary = "상품 수정", description = "상품 업데이트.<br>요청 본문에 ProductUpdateRequest DTO 사용.")
	public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody ProductUpdateRequest productReq) {
		Product updateProduct = productService.updateProduct(id, productReq);
		return ResponseEntity.ok(updateProduct);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "상품 삭제", description = "주어진 id(int)에 해당하는 상품 삭제.<br>데이터가 물리적으로 삭제되지는 않음.<br>(product.product_status=SOLD_OUT, product.stock=0)")
	public ResponseEntity<Void> delProduct(@PathVariable int id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}

}
