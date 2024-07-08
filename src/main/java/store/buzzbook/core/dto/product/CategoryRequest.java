package store.buzzbook.core.dto.product;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CategoryRequest {
	@NotBlank
	private String name;
	@Nullable
	private Integer parentCategory;
}
