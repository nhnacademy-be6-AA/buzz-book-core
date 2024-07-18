package store.buzzbook.core.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.order.Wrapping;

public interface WrappingRepository extends JpaRepository<Wrapping, Integer> {
	Wrapping findByPaper(String paper);
	boolean existsByPaper(String paper);
}
