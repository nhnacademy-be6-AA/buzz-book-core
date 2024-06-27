package store.buzzbook.core.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.Tag;

@NoArgsConstructor
@Getter
public class TagResponse {
	private int id;
	private String name;

	public TagResponse(Tag tag) {
		this.id = tag.getId();
		this.name = tag.getName();
	}
}
