package store.buzzbook.core.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ReadWrappingResponse {
	private int id;
	private String paper;
	private int price;
	private boolean deleted;
}
