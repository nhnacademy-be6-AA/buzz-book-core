package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lombok.NonNull;
import store.buzzbook.core.entity.product.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Book findByProductId(int productId);
	boolean existsByIsbn(@NonNull String isbn);

	List<Book> findAllByProductIdIn(List<Integer> productIdList);

	List<Book> findAllByProductIdIsNotNull();

	Page<Book> findAllByProductIdIsNotNull(Pageable pageable);

	@NonNull
	Page<Book> findAll(@NonNull Pageable pageable);

}
