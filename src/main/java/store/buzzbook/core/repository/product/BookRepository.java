package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.buzzbook.core.entity.product.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Book findByProductId(int productId);

	List<Book> findAllByProductIdIn(List<Integer> productIdList);

	List<Book> findAllByProductIdIsNotNull();

}
