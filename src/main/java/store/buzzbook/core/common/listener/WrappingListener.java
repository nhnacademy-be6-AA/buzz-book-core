package store.buzzbook.core.common.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.repository.order.WrappingRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class WrappingListener implements ApplicationListener<ApplicationReadyEvent> {
	public static final String UNPACKAGED = "없음";
	public static final String GIFT = "선물포장";
	public static final String NEWSPAPER = "신문지";
	public static final String PAPERBOX = "종이박스";

	private final WrappingRepository wrappingRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (Boolean.FALSE.equals(wrappingRepository.existsByPaper(UNPACKAGED))) {
			Wrapping wrapping = Wrapping.builder()
					.price(0)
					.paper(UNPACKAGED)
					.deleted(false)
					.build();
			wrappingRepository.save(wrapping);
		}

		if (Boolean.FALSE.equals(wrappingRepository.existsByPaper(GIFT))) {
			Wrapping wrapping = Wrapping.builder()
				.price(1000)
				.paper(GIFT)
				.deleted(false)
				.build();
			wrappingRepository.save(wrapping);
		}

		if (Boolean.FALSE.equals(wrappingRepository.existsByPaper(NEWSPAPER))) {
			Wrapping wrapping = Wrapping.builder()
				.price(100)
				.paper(NEWSPAPER)
				.deleted(false)
				.build();
			wrappingRepository.save(wrapping);
		}

		if (Boolean.FALSE.equals(wrappingRepository.existsByPaper(PAPERBOX))) {
			Wrapping wrapping = Wrapping.builder()
				.price(500)
				.paper(PAPERBOX)
				.deleted(false)
				.build();
			wrappingRepository.save(wrapping);
		}
	}
}
