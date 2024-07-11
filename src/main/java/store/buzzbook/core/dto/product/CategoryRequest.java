package store.buzzbook.core.dto.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class CategoryRequest {
	@NotBlank
	private String name;
	@Nullable
	private Integer parentCategory;
	@Nullable
	private List<Integer> subCategories = new ArrayList<>();
}
