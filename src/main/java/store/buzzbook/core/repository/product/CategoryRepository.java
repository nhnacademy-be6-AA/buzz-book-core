package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.core.entity.product.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);
    void deleteById(int id);

    List<Category> findByParentCategoryIsNull();

    List<Category> findAllByParentCategoryId(int parentCategoryId);
}
