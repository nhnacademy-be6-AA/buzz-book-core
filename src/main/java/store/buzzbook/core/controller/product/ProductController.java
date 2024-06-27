package store.buzzbook.core.controller.product;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.ProductRequest;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.product.ProductUpdateRequest;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.service.product.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management API", description = "상품 정보 CRUD API")
public class ProductController {

	private final ProductService productService;

	@PostMapping
	@Operation(summary = "상품 추가", description = "필요 DTO : ProductUpdateRequest")
	public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productReq) {
		ProductResponse saveProduct = productService.saveProduct(productReq);
		return ResponseEntity.ok(saveProduct);
	}

	@GetMapping
	@Operation(summary = "조건으로 상품 목록 조회", description = "입력필요")
	public ResponseEntity<Page<ProductResponse>> getAllProduct(@RequestParam(required = false) Product.StockStatus status,
		@RequestParam(required = false, defaultValue = "0") Integer pageNo,
		@RequestParam(required = false, defaultValue = "10") Integer pageSize) {
		if (status == null) {
			return ResponseEntity.ok(productService.getAllProducts(pageNo, pageSize));
		}
		return ResponseEntity.ok(productService.getAllProductsByStockStatus(status, pageNo, pageSize));
	}

	@GetMapping("/{id}")
	@Operation(summary = "상품 단일 조회", description = "int id로 Product 단일 조회")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable int id) {
		ProductResponse product = productService.getProductById(id);
		return ResponseEntity.ok(product);
	}

	@PutMapping("/{id}")
	@Operation(summary = "상품 수정", description = "필요 DTO : ProductUpdateRequest")
	public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody ProductUpdateRequest productReq) {
		Product updateProduct = productService.updateProduct(id, productReq);
		return ResponseEntity.ok(updateProduct);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "id로 상품 삭제", description = "상품 논리 삭제(Product.stock = 0, Product.product_status = SOLD_OUT)")
	public ResponseEntity<Void> delProduct(@PathVariable int id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<String> handleProductNotFoundException(DataNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
}
