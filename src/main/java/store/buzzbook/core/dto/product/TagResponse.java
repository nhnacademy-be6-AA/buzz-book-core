package store.buzzbook.core.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import store.buzzbook.core.entity.product.Tag;

@AllArgsConstructor
@Getter
public class TagResponse {
	private int id;
	private String name;

	public static TagResponse convertToTagResponse(Tag tag)
	{
		return new TagResponse(tag.getId(), tag.getName());
	}

}
