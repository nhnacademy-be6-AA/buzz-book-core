package store.buzzbook.core.entity.order;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.user.User;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "`order`")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@NotNull
	private String orderStr;
	@NotNull
	private int price;
	private String request;
	@NotNull
	private String address;
	@NotNull
	private String addressDetail;
	@NotNull
	private int zipcode;
	@NotNull
	private LocalDate desiredDeliveryDate;
	@NotNull
	private String receiver;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "id", name = "user_id", nullable = true)
	private User user;
	@NotNull
	private String sender;
	@NotNull
	private String senderContactNumber;
	@NotNull
	private String receiverContactNumber;

	@OneToMany(mappedBy = "order")
	private List<OrderDetail> details = new ArrayList<>();
}
