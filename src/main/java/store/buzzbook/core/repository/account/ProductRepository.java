package store.buzzbook.core.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
