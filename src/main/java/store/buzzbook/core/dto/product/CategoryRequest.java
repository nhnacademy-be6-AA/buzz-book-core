package store.buzzbook.core.dto.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
	@NotBlank
	private String name;
	@Nullable
	private Integer parentCategoryId;
	@Nullable
	private List<Integer> subCategoryIds = new ArrayList<>();
}
