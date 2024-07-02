package store.buzzbook.core.service.product;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.product.CategoryResponse;
import store.buzzbook.core.repository.product.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public List<CategoryResponse> getAllCategoryResponses() {
		return categoryRepository.findAll().stream()
			.map(CategoryResponse::convertToCategoryResponse)
			.collect(Collectors.toList());
	}
}
