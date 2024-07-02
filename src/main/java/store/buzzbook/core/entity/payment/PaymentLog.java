package store.buzzbook.core.entity.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.order.Order;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class PaymentLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "id", name = "order_id", nullable = false)
	private Order order;

	@NotNull
	private String name;
	@NotNull
	private int price;
}
