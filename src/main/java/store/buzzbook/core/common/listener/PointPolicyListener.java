package store.buzzbook.core.common.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.entity.point.PointPolicy;
import store.buzzbook.core.repository.point.PointPolicyRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class PointPolicyListener implements ApplicationListener<ApplicationReadyEvent> {

	private static final String SIGN_UP = "회원가입";
	private static final String REVIEW = "리뷰작성";
	private static final String BOOK = "전체도서";

	private final PointPolicyRepository pointPolicyRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {

		if (!pointPolicyRepository.existsByName(SIGN_UP)) {
			PointPolicy pointPolicy = PointPolicy.builder()
				.name(SIGN_UP)
				.rate(0)
				.point(5000)
				.deleted(false)
				.build();
			pointPolicyRepository.save(pointPolicy);
		}

		if (!pointPolicyRepository.existsByName(REVIEW)) {
			PointPolicy pointPolicy = PointPolicy.builder()
				.name(REVIEW)
				.rate(0)
				.point(500)
				.deleted(false)
				.build();
			pointPolicyRepository.save(pointPolicy);
		}

		if (!pointPolicyRepository.existsByName(BOOK)) {
			PointPolicy pointPolicy = PointPolicy.builder()
				.name(BOOK)
				.rate(0.1)
				.point(0)
				.deleted(false)
				.build();
			pointPolicyRepository.save(pointPolicy);
		}
	}
}