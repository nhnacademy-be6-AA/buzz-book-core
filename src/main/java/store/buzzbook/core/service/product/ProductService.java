package store.buzzbook.core.service.product;

import static store.buzzbook.core.dto.product.response.CategoryResponse.*;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.product.response.ProductRequest;
import store.buzzbook.core.dto.product.response.ProductResponse;
import store.buzzbook.core.dto.product.response.ProductUpdateRequest;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.repository.product.BookRepository;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;

@RequiredArgsConstructor
@Service
public class ProductService {

	private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private BookRepository bookRepository;

	public Product saveProduct(ProductRequest productReq) {
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
		return productRepository.save(product);
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

        //TODO 예외처리
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

	public Book deleteBook(int productId){
        Product product = productRepository.findById(productId).orElse(null);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        ProductUpdateRequest productReq = ProductUpdateRequest.builder()
            .stock(product.getStock())
            .productName(product.getProductName())
            .price(product.getPrice())
            .thumbnailPath(product.getThumbnailPath())
            .stockStatus(Product.StockStatus.SOLD_OUT)
            .build();

        updateProduct(productId, productReq);

        Book book = bookRepository.findByProductId(productId);
        book.setProduct(null);
        return bookRepository.save(book);
	}
}
