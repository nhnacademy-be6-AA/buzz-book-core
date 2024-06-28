package store.buzzbook.core.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductBookResponse {
	ProductResponse product;
	BookResponse book = null;
}
