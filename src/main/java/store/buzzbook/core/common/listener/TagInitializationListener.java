package store.buzzbook.core.common.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.repository.product.TagRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagInitializationListener implements ApplicationListener<ApplicationReadyEvent> {

	public static final String PACKABLE_TAG = "포장가능";
	public static final String PACKABLE_TAG_HAVE_SPACE = "포장 가능";

	private final TagRepository tagRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		initializeTags();
	}

	private void initializeTags() {

		Tag existingTagWithSpace = tagRepository.findByName(PACKABLE_TAG_HAVE_SPACE).orElse(null);

		// "포장 가능" 태그가 있으면 "포장가능" 태그로 수정
		if (existingTagWithSpace != null) {
			existingTagWithSpace = new Tag(existingTagWithSpace.getId(), PACKABLE_TAG);
			tagRepository.save(existingTagWithSpace);
			log.info("태그이름 수정: {} -> {}", PACKABLE_TAG_HAVE_SPACE, PACKABLE_TAG);
		}

		Tag packableTag = tagRepository.findByName(PACKABLE_TAG).orElse(null);

		// "포장 가능" 태그도 수정하고 "포장가능" 태그가 없으면 id=0의 포장가능 태그생성
		if (packableTag == null) {
			packableTag = new Tag(PACKABLE_TAG);
			tagRepository.save(packableTag);
			log.info("포장가능 태그가 없어 생성함.");
		}
	}
}
