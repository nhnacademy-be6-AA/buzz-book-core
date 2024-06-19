package store.buzzbook.core.entity.payment;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@EntityListeners(AuditingEntityListener.class)
public class BillLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String payment;
	private int price;

	@CreatedDate
	private ZonedDateTime paymentDate;

	@OneToOne
	@JoinColumn(referencedColumnName = "id", name = "order_id", nullable = false)
	private Order order;

	private BillStatus status;

	private UUID paymentKey;
}
