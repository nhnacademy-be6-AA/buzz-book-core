package store.buzzbook.core.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "product_tag"
	// , uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "tag_id"})}
)
public class ProductTag {

	@Id
	@Column(name = "product_id")
	private int productId;

	@Id
	@Column(name = "tag_id")
	private int tagId;

	@ManyToOne
	@MapsId("productId")
	@JoinColumn(name = "product_id", insertable = false, updatable = false)
	private Product product;

	@ManyToOne
	@MapsId("tagId")
	@JoinColumn(name = "tag_id", insertable = false, updatable = false)
	private Tag tag;
}
