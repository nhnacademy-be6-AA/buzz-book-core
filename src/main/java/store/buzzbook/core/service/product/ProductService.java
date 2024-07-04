package store.buzzbook.core.service.product;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.document.product.ProductDocument;
import store.buzzbook.core.dto.product.CategoryResponse;
import store.buzzbook.core.dto.product.ProductRequest;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.product.ProductUpdateRequest;
import store.buzzbook.core.dto.product.TagResponse;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.ProductTag;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.ProductTagRepository;
import store.buzzbook.core.repository.product.TagRepository;
import store.buzzbook.core.repository.product.elastic.ProductDocumentRepository;

@RequiredArgsConstructor
@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;
	private final ProductTagRepository productTagRepository;

	// // Elasticsearch 용 리포지토리
	private final ProductDocumentRepository productDocumentRepository;

	@Transactional
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

		// Elasticsearch 저장
		productDocumentRepository.save(new ProductDocument(product));

		return convertToProductResponse(product);
	}


	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream()
			.map(ProductResponse::convertToProductResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProductsByStockStatus(Product.StockStatus stockStatus) {
		return productRepository.findAllByStockStatus(stockStatus).stream()
			.map(ProductResponse::convertToProductResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public Page<ProductResponse> getAllProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAll(pageable).map(ProductResponse::convertToProductResponse);
	}

	@Transactional(readOnly = true)
	public Page<ProductResponse> getAllProductsByStockStatus(Product.StockStatus stockStatus, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAllByStockStatus(stockStatus, pageable)
			.map(ProductResponse::convertToProductResponse);
	}

	@Transactional(readOnly = true)
	public ProductResponse getProductById(int id) {
		Product product = productRepository.findById(id).orElse(null);

		if (product == null) {
			throw new DataNotFoundException("product", id);
		}

		return ProductResponse.convertToProductResponse(product);
	}
	@Transactional
	public ProductResponse updateProduct(int id, ProductUpdateRequest productRequest) {
		Product product = productRepository.findById(id).orElseThrow(() -> new DataNotFoundException("product", id));

		Category category = categoryRepository.findById(productRequest.getCategoryId())
			.orElseThrow(() -> new DataNotFoundException("category", productRequest.getCategoryId()));

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
			category, product.getProductTags());

		updatedProduct = productRepository.save(updatedProduct);

		// Elasticsearch 저장
		productDocumentRepository.save(new ProductDocument(updatedProduct));

		// 태그 업데이트 로직
		if (productRequest.getTags() != null) {
			productTagRepository.deleteByProductId(id); // 기존 태그 삭제
			for (String tagName : productRequest.getTags()) {
				Tag tag = tagRepository.findByName(tagName)
					.orElseGet(() -> tagRepository.save(new Tag(tagName)));
				productTagRepository.save(new ProductTag(updatedProduct, tag));
			}
		}

		return convertToProductResponse(updatedProduct);
	}

	@Transactional
	public Product deleteProduct(int productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new DataNotFoundException("product", productId));

		Product newProduct = new Product(product.getId(), 0, product.getProductName(), product.getDescription(),
			product.getPrice(),
			product.getForwardDate(), product.getScore(), product.getThumbnailPath(), Product.StockStatus.SOLD_OUT,
			product.getCategory(), product.getProductTags());

		productDocumentRepository.save(new ProductDocument(newProduct));

		return productRepository.save(newProduct);
	}

	@Transactional(readOnly = true)
	public Page<ProductResponse> getAllProductByName(String productName, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAllByProductNameContaining(productName, pageable)
			.map(this::convertToProductResponse);
	}

	@Transactional(readOnly = true)
	public Page<ProductResponse> getAllProductsByNameAndStockStatus(String productName, Product.StockStatus stockStatus, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAllByProductNameContainingAndStockStatus(productName, stockStatus, pageable)
			.map(this::convertToProductResponse);
	}

	// Elasticsearch를 통한 검색 메소드
	@Transactional(readOnly = true)
	public List<ProductDocument> searchByProductName(String productName) {
		return productDocumentRepository.findByProductNameContaining(productName);
	}


	// 태그 관련 정보를 포함하는 ProductResponse 변환 메서드
	private ProductResponse convertToProductResponse(Product product) {
		List<TagResponse> tagResponses = product.getProductTags().stream()
			.map(ProductTag::getTag)
			.map(TagResponse::convertToTagResponse)
			.collect(Collectors.toList());

		return ProductResponse.builder()
			.id(product.getId())
			.stock(product.getStock())
			.productName(product.getProductName())
			.description(product.getDescription())
			.price(product.getPrice())
			.forwardDate(product.getForwardDate())
			.score(product.getScore())
			.thumbnailPath(product.getThumbnailPath())
			.stockStatus(product.getStockStatus())
			.category(CategoryResponse.convertToCategoryResponse(product.getCategory()))
			.tags(tagResponses)
			.build();
	}


}
