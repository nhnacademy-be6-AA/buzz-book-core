package store.buzzbook.core.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.core.entity.product.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
}
