package store.buzzbook.core.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductTagRequest {

	private int productId;
	private int tagId;
}

//프론트 -> 프로덕트 태그 리퀘스트 -> 코어에서 받아서