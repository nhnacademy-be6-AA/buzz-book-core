package store.buzzbook.core.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.core.entity.product.BookAuthor;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Integer> {
}
