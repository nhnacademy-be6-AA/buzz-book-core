package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.product.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer> {

	Tag findByName(String name);
	List<Tag> findByProductId(int productId);
}
