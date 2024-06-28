package store.buzzbook.core.service.user.implement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.service.user.GradeService;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
	private static final Logger log = LoggerFactory.getLogger(GradeServiceImpl.class);
	private final GradeRepository gradeRepository;

	@Override
	public void save(Grade grade) {
		boolean isExist = gradeRepository.existsByName(grade.getName());

		if (!isExist) {
			Grade savedGrade = gradeRepository.save(grade);

			log.debug("Grade 저장 : {}", savedGrade.getName());
		}

	}
}
