package store.buzzbook.core.dto.review;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
@Builder
public class ReviewResponse {
	private int id;
	private long userId;
	private String userName;
	private String content;
	@Nullable
	private List<String> picturePath;
	private int reviewScore;
	private LocalDateTime reviewCreatedAt;
	private long orderDetailId;
	private long productId;
}
