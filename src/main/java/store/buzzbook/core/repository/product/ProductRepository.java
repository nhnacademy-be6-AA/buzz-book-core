package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.core.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByThumbnailPath(String thumbnailPath);

    List<Product> findAllByStockStatus(Product.StockStatus stockStatus);
}
