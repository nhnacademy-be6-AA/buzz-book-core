package store.buzzbook.core.entity.product;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.pk.ProductTagPk;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "product_tag",
	uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "tag_id"})})
public class ProductTag {


	@EmbeddedId
	private ProductTagPk pk;

	@ManyToOne
	@MapsId("productId")
	@JoinColumn(name = "product_id", insertable = false, updatable = false)
	private Product product;

	@ManyToOne
	@MapsId("tagId")
	@JoinColumn(name = "tag_id", insertable = false, updatable = false)
	private Tag tag;

	public ProductTag(Product product, Tag tag) {
		this.pk = new ProductTagPk(product.getId(), tag.getId());
		this.product = product;
		this.tag = tag;
	}

}
