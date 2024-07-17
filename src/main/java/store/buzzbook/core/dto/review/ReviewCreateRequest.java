package store.buzzbook.core.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewCreateRequest {

	@NotBlank
	private String content;
	@Min(1)
	@Max(10)
	private int reviewScore;
	@NotNull
	private long orderDetailId;

}
