package store.buzzbook.core.service.product;

import static store.buzzbook.core.dto.product.CategoryResponse.*;
import static store.buzzbook.core.dto.product.ProductResponse.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.ProductRequest;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.product.ProductUpdateRequest;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;
// import store.buzzbook.core.repository.product.elastic.ProductDocumentRepository;

@RequiredArgsConstructor
@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	// // Elasticsearch 용 리포지토리
	// private final ProductDocumentRepository productDocumentRepository;

	public ProductResponse saveProduct(ProductRequest productReq) {
		Category category = categoryRepository.findById(productReq.getCategoryId()).orElse(null);
		Product product = Product.builder()
			.stock(productReq.getStock())
			.productName(productReq.getProductName())
			.description(productReq.getDescription() == null ? null : productReq.getDescription())
			.price(productReq.getPrice())
			.forwardDate(LocalDate.parse(productReq.getForwardDate()))
			.score(0)
			.thumbnailPath(productReq.getThumbnailPath())
			.stockStatus(productReq.getStockStatus())
			.category(category)
			.build();
		product = productRepository.save(product);

		// productDocumentRepository.save(new ProductDocument(product));

		return convertToProductResponse(product);
	}

	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream()
			.map(ProductResponse::convertToProductResponse)
			.toList();
	}

	public List<ProductResponse> getAllProductsByStockStatus(Product.StockStatus stockStatus) {
		return productRepository.findAllByStockStatus(stockStatus).stream()
			.map(ProductResponse::convertToProductResponse)
			.toList();
	}

	public Page<ProductResponse> getAllProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAll(pageable).map(ProductResponse::convertToProductResponse);
	}

	public Page<ProductResponse> getAllProductsByStockStatus(Product.StockStatus stockStatus, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAllByStockStatus(stockStatus, pageable)
			.map(ProductResponse::convertToProductResponse);
	}

	public ProductResponse getProductById(int id) {
		Product product = productRepository.findById(id).orElse(null);

		if (product == null) {
			throw new DataNotFoundException("product", id);
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
		if (product == null) {
			throw new DataNotFoundException("product", id);
		}

		Category category = categoryRepository.findById(productRequest.getCategoryId()).orElse(null);
		if (category == null) {
			throw new DataNotFoundException("category", productRequest.getCategoryId());
		}

		Product updatedProduct = new Product(
			product.getId(),
			productRequest.getStock(),
			productRequest.getProductName(),
			productRequest.getDescription() == null ? product.getDescription() : productRequest.getDescription(),
			productRequest.getPrice(),
			product.getForwardDate(),
			product.getScore(),
			product.getThumbnailPath(),
			productRequest.getStockStatus(),
			category, null);

		return productRepository.save(updatedProduct);
	}

	public Product deleteProduct(int productId) {

		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new DataNotFoundException("product", productId));

		Product newProduct = new Product(product.getId(), 0, product.getProductName(), product.getDescription(),
			product.getPrice(),
			product.getForwardDate(), product.getScore(), product.getThumbnailPath(), Product.StockStatus.SOLD_OUT,
			product.getCategory(), product.getProductTags());

		// productDocumentRepository.save(new ProductDocument(newProduct));

		return productRepository.save(newProduct);
	}

	public List<ProductResponse> searchProductsByName(String productName) {
		return productRepository.findByProductNameContaining(productName).stream()
			.map(ProductResponse::convertToProductResponse)
			.toList();
	}

	// // Elasticsearch를 통한 검색 메소드
	// public List<ProductDocument> searchByProductName(String productName) {
	// 	return productDocumentRepository.findByProductNameContaining(productName);
	// }

	// public List<ProductDocument> searchByCategoryName(String categoryName) {
	// 	return productDocumentRepository.findByCategory_name(categoryName);
	// }

}
