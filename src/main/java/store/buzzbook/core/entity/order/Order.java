package store.buzzbook.core.entity.order;

import java.time.LocalDate;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
	@Size(min = 1, max = 50)
	@NotNull
	private String orderStr;
	@NotNull
	private int price;
	@Size(min = 1, max = 200)
	private String request;
	@Size(min = 1, max = 200)
	@NotNull
	private String address;
	@Size(min = 1, max = 200)
	@NotNull
	private String addressDetail;
	@NotNull
	private int zipcode;
	@NotNull
	private LocalDate desiredDeliveryDate;
	@Size(min = 1, max = 20)
	@NotNull
	private String receiver;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "id", name = "user_id", nullable = true)
	private User user;
	@Size(min = 1, max = 20)
	@NotNull
	private String sender;
	@NotNull
	private String senderContactNumber;
	@NotNull
	private String receiverContactNumber;

	@Size(min = 1, max = 255)
	private String orderEmail;
	
	@Size(min = 1, max = 20)
	private String couponCode;

	private Integer deliveryRate;

	@OneToMany(mappedBy = "order")
	private List<OrderDetail> details = new ArrayList<>();
}
