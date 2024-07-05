package store.buzzbook.core.repository.product;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import lombok.NonNull;
import store.buzzbook.core.entity.product.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer> {

	Page<Tag> findAll(@NonNull Pageable pageable);

	Optional<Tag> findByName(String name);

	Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
