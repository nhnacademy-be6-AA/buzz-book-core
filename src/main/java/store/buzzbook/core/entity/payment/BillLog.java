package store.buzzbook.core.entity.payment;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@EntityListeners(AuditingEntityListener.class)
public class BillLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	private String payment;

	@NotNull
	private int price;

	@NotNull
	@CreatedDate
	private LocalDateTime payAt;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "id", name = "order_id", nullable = false)
	private Order order;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	private BillStatus status;

	@NotNull
	private String paymentKey;

	private String cancelReason;
}
