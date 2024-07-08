package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import store.buzzbook.core.entity.product.ProductTag;
import store.buzzbook.core.entity.product.pk.ProductTagPk;

public interface ProductTagRepository extends JpaRepository<ProductTag, ProductTagPk> {

	List<ProductTag> findByProductId(int productId);
	void deleteByProductIdAndTagId(int productId, int tagId);

	@Transactional
	@Modifying
	@Query("DELETE FROM ProductTag pt WHERE pt.product.id = :productId")
	void deleteByProductId(@Param("productId") int productId);

	@Transactional
	@Modifying
	@Query("DELETE FROM ProductTag pt WHERE pt.product.id = :productId AND pt.tag.id IN :tagIds")
	void deleteTagsByProductIdAndTagIds(@Param("productId") int productId, @Param("tagIds") List<Integer> tagIds);
}
