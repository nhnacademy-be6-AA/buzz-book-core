package store.buzzbook.core.common.listener;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.repository.product.CategoryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryInitializationListener implements ApplicationListener<ApplicationReadyEvent> {

	public static final String ROOT_CATEGORY = "전체";
	public static final int ROOT_CATEGORY_ID = 0;
	public static final String KOREAN_BOOKS_CATEGORY = "국내도서";

	private final CategoryRepository categoryRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		initializeCategories();
	}

	private void initializeCategories() {
		initializeZeroIdCategory();
		initializeRootCategory();
	}

	// id가 0인 카테고리가 있으면 id를 바꿈
	private void initializeZeroIdCategory() {
		Category idZeroCategory = categoryRepository.findById(0).orElse(null);
		if (idZeroCategory != null && !idZeroCategory.getName().equals(ROOT_CATEGORY)) {
			Category tempCategory = new Category(idZeroCategory.getName(), idZeroCategory.getParentCategory(),
				idZeroCategory.getSubCategories());
			categoryRepository.save(tempCategory);
		}
	}

	// '전체'에 해당하는 카테고리를 확인하고 처리
	private void initializeRootCategory() {

		Category koreanBooksCategory = categoryRepository.findByName(KOREAN_BOOKS_CATEGORY);
		if (koreanBooksCategory == null) {
			log.warn(KOREAN_BOOKS_CATEGORY + " 카테고리가 없음");
		}

		Category rootCategory = categoryRepository.findByName(ROOT_CATEGORY);
		if (rootCategory == null
			|| rootCategory.getId() != ROOT_CATEGORY_ID
			|| rootCategory.getParentCategory() != null) {
			rootCategory = new Category(ROOT_CATEGORY_ID, ROOT_CATEGORY, null,
				(koreanBooksCategory == null) ? List.of() : List.of(koreanBooksCategory));
			categoryRepository.save(rootCategory);
			log.info("전체 카테고리가 없어서 생성함.");
		}
	}
}
