package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.product.ProductTag;
import store.buzzbook.core.entity.product.pk.ProductTagPk;

public interface ProductTagRepository extends JpaRepository<ProductTag, ProductTagPk> {

	List<ProductTag> findByProductId(int productId);
	void deleteByProductIdAndTagId(int productId, int tagId);

}
