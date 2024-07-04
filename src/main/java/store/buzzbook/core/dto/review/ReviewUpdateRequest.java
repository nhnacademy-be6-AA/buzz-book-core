package store.buzzbook.core.dto.review;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReviewUpdateRequest {

	@NotNull
	private int id;

	@NotBlank(message = "내용을 입력하세요.")
	private String content;

	@Nullable
	private String picturePath;

	@NotNull(message = "점수는 1점에서 10점 자연수값만 입력가능합니다.")
	@Size(min = 1, max = 10)
	private int reviewScore;

	@NotNull
	private long orderDetailId;

}
