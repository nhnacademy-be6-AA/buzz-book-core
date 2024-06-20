package store.buzzbook.core.service.user.implement;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.user.UnknownUserException;
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
			if (!savedGrade.getName().equals(grade.getName())
				|| !Objects.equals(savedGrade.getBenefit(), grade.getBenefit())
				|| !Objects.equals(savedGrade.getStandard(), grade.getStandard())) {
				log.error("Grade 저장 중 알 수 없는 오류가 발생했습니다.");
				throw new UnknownUserException("Grade 저장 중 알 수 없는 오류가 발생했습니다.");
			}
			log.info("Grade 저장 : {}", savedGrade.getName());
		}

	}
}
