package store.buzzbook.core.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Failure {
	private String code;
	private String message;
}
