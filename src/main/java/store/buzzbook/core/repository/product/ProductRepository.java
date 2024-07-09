package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import store.buzzbook.core.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	Product findByThumbnailPath(String thumbnailPath);

	List<Product> findAllByStockStatus(Product.StockStatus stockStatus);

	List<Product> findByCategoryId(Integer categoryId);

	List<Product> findAllByProductNameContaining(String productName);

	//상품의 판매 상태가 일치하는 Product
	Page<Product> findAllByStockStatus(Product.StockStatus stockStatus, Pageable pageable);

	//상품의 이름을 포함하는 Product
	Page<Product> findAllByProductNameContaining(String productName, Pageable pageable);

	//상품의 판매 상태가 일치하고 이름을 포함하는 Product
	Page<Product> findAllByProductNameContainingAndStockStatus(String productName, Product.StockStatus stockStatus,
		Pageable pageable);

	//상품을 이름순으로 정렬
	Page<Product> findAllByOrderByProductNameDesc(Pageable pageable);

	//상품의 판매 상태가 일치하고 이름순으로 정렬
	Page<Product> findAllByStockStatusOrderByProductNameDesc(Product.StockStatus stockStatus, Pageable pageable);

	//조건으로 검색한 상품을 리뷰수가 많은 순서대로 정렬
	@Query("SELECT p FROM Product p " +
		"LEFT JOIN OrderDetail od ON p.id = od.product.id " +
		"LEFT JOIN Review r ON od.id = r.orderDetail.id " +
		"WHERE (:name IS NULL OR p.productName LIKE %:name%) " +
		"AND (:status IS NULL OR p.stockStatus = :status) " +
		"AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
		"GROUP BY p.id " +
		"ORDER BY COUNT(r.id) DESC")
	Page<Product> findProductsByCriteriaOrderByReviewCountDesc(Product.StockStatus status, String name,
		Integer categoryId, Pageable pageable);

	Page<Product> findAll(Specification<Product> spec, Pageable pageable);

	@Query("SELECT DISTINCT b, r " +
		"FROM Product p " +
		"JOIN Book b ON p.id = b.product.id " +
		"LEFT JOIN OrderDetail od ON p.id = od.product.id " +
		"JOIN Review r ON p.id = r.orderDetail.product.id " +
		"WHERE p.id = :productId " +
		"ORDER BY r.reviewCreatedAt DESC " +
		"LIMIT 5 "
	)
	List<Object[]> findProductDetailWithReviews(@Param("productId") int productId);

}

