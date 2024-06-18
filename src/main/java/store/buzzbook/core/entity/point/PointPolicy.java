package store.buzzbook.core.entity.point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class PointPolicy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Setter
	@Column(nullable = false)
	private String name;

	@Builder.Default
	@Setter
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal point = BigDecimal.valueOf(0.00);

	@Builder.Default
	@Setter
	@Column(nullable = false)
	@ColumnDefault("1.0")
	private double rate = 1.0;
}
