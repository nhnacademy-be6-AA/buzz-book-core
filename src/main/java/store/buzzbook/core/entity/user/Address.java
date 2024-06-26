package store.buzzbook.core.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Column(name = "address")
	@Size(min = 1, max = 255)
	private String address;

	@NotNull
	@Column(name = "detail")
	@Size(min = 1, max = 255)
	private String detail;

	@NotNull
	@Column(name = "zipcode")
	@Max(99999)
	@Min(0)
	private int zipcode;

	@NotNull
	@Column(name = "nation")
	private String nation;

	@NotNull
	@Column(name = "alias")
	@Size(min = 1, max = 20)
	private String alias;
}
