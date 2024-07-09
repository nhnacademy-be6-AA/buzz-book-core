package store.buzzbook.core.dto.product;

import java.util.List;

import org.springframework.lang.Nullable;

import lombok.Builder;
import lombok.Getter;
import store.buzzbook.core.dto.review.ReviewResponse;

@Getter
@Builder
public class ProductDetailResponse {
	@Nullable
	BookResponse book;
	List<ReviewResponse> reviews;
}
