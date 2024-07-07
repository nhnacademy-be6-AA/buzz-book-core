package store.buzzbook.core.service.user.implement;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.service.user.GradeService;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeServiceImpl implements GradeService {
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
