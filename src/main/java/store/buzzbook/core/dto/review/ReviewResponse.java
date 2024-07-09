package store.buzzbook.core.dto.review;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ReviewResponse {
	private int id;
	private long userId;
	private String userName;
	private String content;
	private String picturePath;
	private int reviewScore;
	private LocalDateTime reviewCreatedAt;
	private long orderDetail;
}
