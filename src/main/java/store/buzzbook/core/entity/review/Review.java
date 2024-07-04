package store.buzzbook.core.entity.review;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.order.OrderDetail;

@Getter
@Table(name = "review")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String content;

	@Column
	private String picturePath;

	@Column(nullable = false)
	private int reviewScore;

	@Column(nullable = false)
	private LocalDateTime reviewCreatedAt;

	@OneToOne
	@JoinColumn(name = "order_detail_id", unique = true, nullable = false)
	private OrderDetail orderDetail;

	public Review(String content, String picturePath, int reviewScore, OrderDetail orderDetail) {
		this.content = content;
		this.picturePath = picturePath;
		this.reviewScore = reviewScore;
		this.reviewCreatedAt = LocalDateTime.now();
		this.orderDetail = orderDetail;
	}
}
