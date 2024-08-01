package store.buzzbook.core.service.product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.CategoryResponse;
import store.buzzbook.core.dto.product.ProductRequest;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.product.ProductUpdateRequest;
import store.buzzbook.core.dto.product.TagResponse;
import store.buzzbook.core.elastic.client.ElasticSearchClient;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.ProductTag;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.ProductTagRepository;
import store.buzzbook.core.repository.product.TagRepository;

@RequiredArgsConstructor
@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;
	private final ProductTagRepository productTagRepository;
	private final ProductSpecification productSpecification;
	private final ElasticSearchClient elasticSearchClient;

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final ObjectMapper objectMapper;

	@Value("${spring.elasticsearch.username}")
	private String username;

	@Value("${spring.elasticsearch.password}")
	private String password;

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


		return convertToProductResponse(product);
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream()
			.map(ProductResponse::convertToProductResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public Page<ProductResponse> getAllProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAll(pageable).map(ProductResponse::convertToProductResponse);
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

		// // Elasticsearch 저장
		// productDocumentRepository.save(new ProductDocument(updatedProduct));

		// 태그 업데이트 로직
		if (productRequest.getTags() != null) {
			productTagRepository.deleteByProductId(id);
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


		return productRepository.save(newProduct);
	}

	//엘라 대체용으로 임시로 만듦
	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProductsByTitle(String title) {

		List<Product> products = productRepository.findAllByProductNameContaining(title);

		return products.stream()
			.map(this::convertToProductResponse)
			.toList();
	}


	private String createAuthToken() {
		return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}

	//엘라스틱으로 받아온 값을 objectMapper로 변환해서 book json에서 product id값만 list로 가져오기
	@Transactional(readOnly = true)
	public Page<ProductResponse> getProductsByCriteria(Product.StockStatus status, String name, String elasticName,
		Integer categoryId, String orderBy, int pageNo, int pageSize) {
		List<Integer> productIds = new ArrayList<>();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		if (elasticName != null) {
			String authHeader = createAuthToken();

			String searchResult = elasticSearchClient.searchProducts(elasticName, authHeader, pageNo, 1000);

			try {
				JsonNode rootNode = objectMapper.readTree(searchResult);
				JsonNode hitsNode = rootNode.path("hits").path("hits");

				for (JsonNode hitNode : hitsNode) {
					JsonNode sourceNode = hitNode.path("_source");
					Integer productId = sourceNode.path("productId").asInt();
					productIds.add(productId);
				}

				// 추출한 productId로 제품 데이터 조회
				// Page<Product> products = productRepository.findByIdIn(productIds, pageable);

			} catch (JsonProcessingException e) {
				e.getStackTrace();
				return Page.empty(pageable);
			}
		}

		Page<Product> products;
		if ("reviews".equals(orderBy)) {
			products = productRepository.findProductsByCriteriaOrderByReviewCountDesc(status, name, categoryId,
				pageable);
		} else {

			Specification<Product> spec = Specification.where(
					productSpecification.getProductsByCriteria(status, name, categoryId, productIds)
				.and(productSpecification.orderBy(orderBy)));
			products = productRepository.findAll(spec, pageable);
		}

		return products.map(this::convertToProductResponse);

	}

	// 태그 관련 정보를 포함하는 ProductResponse 변환 메서드
	private ProductResponse convertToProductResponse(Product product) {
		List<TagResponse> tagResponses = product.getProductTags().stream()
			.map(ProductTag::getTag)
			.map(TagResponse::convertToTagResponse)
			.toList();

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
			.category(new CategoryResponse(product.getCategory()))
			.tags(tagResponses)
			.build();
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> getLatestProducts(int count) {
		Pageable pageable = PageRequest.of(0, count);
		return productRepository.findProductsByLatestForwardDate(pageable).stream()
			.map(this::convertToProductResponse)
			.collect(Collectors.toList());
	}
}
