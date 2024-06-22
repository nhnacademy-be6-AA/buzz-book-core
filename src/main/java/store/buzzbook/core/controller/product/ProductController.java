package store.buzzbook.core.controller.product;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.ProductNotFoundException;
import store.buzzbook.core.dto.product.response.ProductRequest;
import store.buzzbook.core.dto.product.response.ProductResponse;
import store.buzzbook.core.dto.product.response.ProductUpdateRequest;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.service.product.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management API", description = "상품 정보 CRUD API")
public class ProductController {

	private final ProductService productService;

	@PostMapping
	@Operation(summary = "상품 추가", description = "상품 데이터를 추가합니다.")
	public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productReq) {
		ProductResponse saveProduct = productService.saveProduct(productReq);
		return ResponseEntity.ok(saveProduct);
	}

	@GetMapping
	@Operation(summary = "모든 상품조회", description = "상품 데이터를 전부 가져옵니다.")
	public ResponseEntity<List<ProductResponse>> getAllProduct() {
		return ResponseEntity.ok(productService.getAllProducts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable int id) {
		ProductResponse product = productService.getProductById(id);
		return ResponseEntity.ok(product);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody ProductUpdateRequest productReq) {
		Product updateProduct = productService.updateProduct(id, productReq);
		return ResponseEntity.ok(updateProduct);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delProduct(@PathVariable int id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
}