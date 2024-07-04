package store.buzzbook.core.entity.point;

import org.hibernate.annotations.ColumnDefault;

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

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class PointPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@Builder.Default
	private int point = 0;

	@Column(nullable = false)
	@Builder.Default
	@ColumnDefault("1.0")
	private double rate = 1.0;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean deleted;

	public void changePoint(int point) {
		this.point = point;
	}

	public void changeRate(double rate) {
		this.rate = rate;
	}

	public void delete() {
		this.deleted = true;
	}
}
