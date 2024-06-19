package store.buzzbook.core.entity.order;

import java.time.ZonedDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.Product;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
public class OrderDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private int price;
	private int quantity;
	private boolean wrap;

	@CreatedDate
	private ZonedDateTime createDate;

	@OneToOne
	@JoinColumn(referencedColumnName = "id", name = "order_status_id", nullable = false)
	private OrderStatus orderStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "id", name = "wrapping_id")
	private Wrapping wrapping;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "product_id", nullable = false)
	private Product product;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "order_id", nullable = false)
	private Order order;
}
