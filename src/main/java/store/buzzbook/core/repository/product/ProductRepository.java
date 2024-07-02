package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.NonNull;
import store.buzzbook.core.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByThumbnailPath(String thumbnailPath);

    List<Product> findAllByStockStatus(Product.StockStatus stockStatus);

    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

    Page<Product> findAllByStockStatus(Product.StockStatus stockStatus, Pageable pageable);

    List<Product> findByProductNameContaining(String productName);
}
