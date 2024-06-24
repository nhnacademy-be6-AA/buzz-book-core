package store.buzzbook.core.service.product;

import static store.buzzbook.core.dto.product.response.CategoryResponse.*;
import static store.buzzbook.core.dto.product.response.ProductResponse.*;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.ProductNotFoundException;
import store.buzzbook.core.dto.product.response.ProductRequest;
import store.buzzbook.core.dto.product.response.ProductResponse;
import store.buzzbook.core.dto.product.response.ProductUpdateRequest;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.repository.product.BookRepository;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;

@RequiredArgsConstructor
@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final BookRepository bookRepository;

	public ProductResponse saveProduct(ProductRequest productReq) {
		Category category = categoryRepository.findById(productReq.getCategoryId()).orElse(null);
		Product product = Product.builder()
			.stock(productReq.getStock())
			.productName(productReq.getProductName())
			.price(productReq.getPrice())
			.forwardDate(productReq.getForwardDate())
			.score(productReq.getScore())
			.thumbnailPath(productReq.getThumbnailPath())
			.stockStatus(productReq.getStockStatus())
			.category(category)
			.build();
		product = productRepository.save(product);
		return convertToProductResponse(product);
	}

	public List<ProductResponse> getAllProducts() {

		return productRepository.findAll().stream()
			.map(ProductResponse::convertToProductResponse)
			.toList();
	}

	public ProductResponse getProductById(int id) {
		Product product = productRepository.findById(id).orElse(null);

		if (product == null) {
			throw new RuntimeException("Product not found");
		}

		return ProductResponse.builder()
			.id(product.getId())
			.stock(product.getStock())
			.productName(product.getProductName())
			.price(product.getPrice())
			.forwardDate(product.getForwardDate())
			.score(product.getScore())
			.thumbnailPath(product.getThumbnailPath())
			.stockStatus(product.getStockStatus())
			.category(convertToCategoryResponse(product.getCategory()))
			.build();
	}

	public Product updateProduct(int id, ProductUpdateRequest productRequest) {
		Product product = productRepository.findById(id).orElse(null);
		Category category = categoryRepository.findById(productRequest.getCategoryId()).orElse(null);

		if (product == null) {
			throw new RuntimeException("Product not found");
		}
		if (category == null) {
			throw new RuntimeException("Category not found");
		}

		Product updatedProduct = new Product(
			product.getId(),
			productRequest.getStock(),
			productRequest.getProductName(),
			productRequest.getPrice(),
			product.getForwardDate(),
			product.getScore(),
			product.getThumbnailPath(),
			productRequest.getStockStatus(),
			category);

		return productRepository.save(updatedProduct);
	}

	public void deleteProduct(int productId) {
		if (!productRepository.existsById(productId)) {
			throw new ProductNotFoundException("존재하지 않는 상품입니다. id : " + productId);
		}
		productRepository.deleteById(productId);
	}
}
