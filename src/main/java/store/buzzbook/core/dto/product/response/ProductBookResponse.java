package store.buzzbook.core.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductBookResponse {
	ProductResponse product;
	BookResponse book = null;
}
