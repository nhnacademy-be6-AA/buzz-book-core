package store.buzzbook.core.entity.product.pk;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
public class BookAuthorPk {

	private long bookId;
	private int authorId;
}
