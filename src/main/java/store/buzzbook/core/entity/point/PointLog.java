package store.buzzbook.core.entity.point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.account.domain.user.User;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class PointLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private User user;

	@Column(nullable = false)
	private ZonedDateTime createDate;

	@Column(nullable = false)
	private String inquiry;

	@Column(nullable = false, precision = 10, scale = 2, columnDefinition = "default `0.00`")
	private BigDecimal delta;

	@Column(nullable = false, precision = 10, scale = 2, columnDefinition = "default `0.00`")
	private BigDecimal balance;
}
