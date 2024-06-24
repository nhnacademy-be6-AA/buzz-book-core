package store.buzzbook.core.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.core.entity.product.Author;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Author findByName(String name);


}
