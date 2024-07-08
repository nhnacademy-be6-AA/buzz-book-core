package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import lombok.NonNull;
import store.buzzbook.core.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByThumbnailPath(String thumbnailPath);

    List<Product> findAllByStockStatus(Product.StockStatus stockStatus);

    List<Product> findByCategoryId(Integer categoryId);

    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

    Page<Product> findAllBy(Specification<Product> spec, Pageable pageable);

    //상품의 판매 상태가 일치하는 Product
    Page<Product> findAllByStockStatus(Product.StockStatus stockStatus, Pageable pageable);

    //상품의 이름을 포함하는 Product
    Page<Product> findAllByProductNameContaining(String productName, Pageable pageable);

    //상품의 판매 상태가 일치하고 이름을 포함하는 Product
    Page<Product> findAllByProductNameContainingAndStockStatus(String productName, Product.StockStatus stockStatus, Pageable pageable);

    //상품을 이름순으로 정렬
    Page<Product> findAllByOrderByProductNameDesc(Pageable pageable);

    //상품의 판매 상태가 일치하고 이름순으로 정렬
    Page<Product> findAllByStockStatusOrderByProductNameDesc(Product.StockStatus stockStatus, Pageable pageable);

    //상품을 리뷰수가 많은 순서대로 정렬
    @Query("SELECT p FROM Review r JOIN r.orderDetail od JOIN od.product p GROUP BY p.id ORDER BY COUNT(r.id) DESC")
    Page<Product> findProductsOrderByReviewCountDesc(Pageable pageable);



}
