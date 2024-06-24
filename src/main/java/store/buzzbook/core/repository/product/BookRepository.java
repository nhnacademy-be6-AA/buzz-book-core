package store.buzzbook.core.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.buzzbook.core.entity.product.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Book findByProductId(int productId);
}
