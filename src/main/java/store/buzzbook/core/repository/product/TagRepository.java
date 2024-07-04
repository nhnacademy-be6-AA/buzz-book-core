package store.buzzbook.core.repository.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.product.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer> {

	List<Tag> findByProductId(int productId);
	Optional<Tag> findByName(String name);
}
