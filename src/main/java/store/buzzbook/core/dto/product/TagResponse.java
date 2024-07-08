package store.buzzbook.core.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import store.buzzbook.core.entity.product.Tag;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TagResponse {

	private final Integer id;
	private final String name;

	public static TagResponse convertToTagResponse(Tag tag) {
		return TagResponse.builder()
			.id(tag.getId())
			.name(tag.getName())
			.build();
	}
}
