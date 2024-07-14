package store.buzzbook.core.service.user;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import store.buzzbook.core.dto.user.GradeInfoResponse;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.implement.GradeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class GradeServiceTest {
	@Mock
	private GradeRepository gradeRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private GradeServiceImpl gradeService;

	private Grade grade;
	private GradeInfoResponse gradeInfoResponse;

	@BeforeEach
	void setUp() {
		grade = Grade.builder().name(GradeName.NORMAL)
			.benefit(0.5d)
			.standard(10000)
			.build();
		gradeInfoResponse = grade.toResponse();
	}

	@Test
	void testSave_NewGrade() {
		Mockito.when(gradeRepository.existsByName(grade.getName())).thenReturn(false);
		Mockito.when(gradeRepository.save(grade)).thenReturn(grade);

		gradeService.save(grade);

		Mockito.verify(gradeRepository, Mockito.times(1)).existsByName(grade.getName());
		Mockito.verify(gradeRepository, Mockito.times(1)).save(grade);
	}

	@Test
	void testSave_ExistingGrade() {
		Mockito.when(gradeRepository.existsByName(grade.getName())).thenReturn(true);

		gradeService.save(grade);

		Mockito.verify(gradeRepository, Mockito.times(1)).existsByName(grade.getName());
		Mockito.verify(gradeRepository, Mockito.never()).save(Mockito.any(Grade.class));
	}

	@Test
	void testGetGradeInfoList() {
		Mockito.when(gradeRepository.findAll()).thenReturn(List.of(grade));

		List<GradeInfoResponse> gradeInfoList = gradeService.getGradeInfoList();

		Assertions.assertNotNull(gradeInfoList);
		Assertions.assertEquals(1, gradeInfoList.size());
		Assertions.assertEquals(gradeInfoResponse, gradeInfoList.get(0));

		Mockito.verify(gradeRepository, Mockito.times(1)).findAll();
	}
}
