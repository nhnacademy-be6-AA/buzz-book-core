package store.buzzbook.core.service.user.implement;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.dto.user.GradeInfoResponse;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.GradeService;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeServiceImpl implements GradeService {
	private final GradeRepository gradeRepository;
	private final UserRepository userRepository;

	@Transactional
	@Override
	public void save(Grade grade) {
		boolean isExist = gradeRepository.existsByName(grade.getName());

		if (!isExist) {
			Grade savedGrade = gradeRepository.save(grade);

			log.debug("Grade 저장 : {}", savedGrade.getName());
		}

	}

	@Transactional
	@Override
	public List<GradeInfoResponse> getGradeInfoList() {
		return gradeRepository.findAll().stream().map(Grade::toResponse).toList();
	}

}
