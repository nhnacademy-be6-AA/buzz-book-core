package store.buzzbook.core.dto.review;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.buzzbook.core.entity.review.Review;

@Getter
@Setter
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ReviewResponse {
	private int id;
	private String content;
	private String picturePath;
	private int reviewScore;
	private LocalDateTime reviewCreatedAt;
	private long orderDetail;

	public ReviewResponse(Review review) {
		id = review.getId();
		content = review.getContent();
		picturePath = review.getPicturePath();
		reviewScore = review.getReviewScore();
		reviewCreatedAt = review.getReviewCreatedAt();
		orderDetail = review.getOrderDetail().getId();
	}
}
