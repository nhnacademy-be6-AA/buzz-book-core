package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import store.buzzbook.core.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	Product findByThumbnailPath(String thumbnailPath);

	List<Product> findByCategoryId(Integer categoryId);

	List<Product> findAllByProductNameContaining(String productName);

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

	@Query("SELECT p FROM Product p ORDER BY p.forwardDate DESC")
	List<Product> findProductsByLatestForwardDate(Pageable pageable);

}

