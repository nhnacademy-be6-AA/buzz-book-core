package store.buzzbook.core.repository.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.repository.account.GradeRepository;

@DataJpaTest
class GradeRepositoryTest {
	@Autowired
	private GradeRepository gradeRepository;
	private Grade grade;

	@BeforeEach
	public void setUp() {
		grade = Grade.builder()
			.benefit(2.5)
			.name("플래티넘")
			.standard(200000)
			.build();

		gradeRepository.save(grade);
	}

	@Test
	@DisplayName("유저 등급 레포지토리 저장 테스트")
	void testSaveAndFind(){
		Grade resultGrade = gradeRepository.findById(grade.getId()).orElse(null);

		Assertions.assertNotNull(resultGrade);
		Assertions.assertEquals(grade.getId(), resultGrade.getId());
		Assertions.assertEquals(grade.getName(), resultGrade.getName());
		Assertions.assertEquals(grade.getStandard(), resultGrade.getStandard());
		Assertions.assertEquals(grade.getBenefit(), resultGrade.getBenefit());
	}



}
