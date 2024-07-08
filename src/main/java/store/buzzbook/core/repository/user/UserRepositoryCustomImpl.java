package store.buzzbook.core.repository.user;

import static store.buzzbook.core.entity.user.QGrade.*;
import static store.buzzbook.core.entity.user.QGradeLog.*;
import static store.buzzbook.core.entity.user.QUser.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.User;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<Grade> findGradeByLoginId(String loginId) {

		Grade targetGrade = jpaQueryFactory.select(grade)
			.from(user)
			.innerJoin(gradeLog)
			.on(user.id.eq(gradeLog.user.id))
			.innerJoin(grade)
			.on(grade.id.eq(gradeLog.grade.id))
			.where(user.loginId.eq(loginId))
			.orderBy(gradeLog.changeAt.desc())
			.fetchFirst();

		return Optional.ofNullable(targetGrade);
	}

	@Override
	public Optional<Grade> findGradeByUserId(Long userId) {
		Grade targetGrade = jpaQueryFactory.select(grade)
			.from(user)
			.innerJoin(gradeLog)
			.on(user.id.eq(gradeLog.user.id))
			.innerJoin(grade)
			.on(grade.id.eq(gradeLog.grade.id))
			.where(user.id.eq(userId))
			.orderBy(gradeLog.changeAt.desc())
			.fetchFirst();

		return Optional.ofNullable(targetGrade);
	}

	@Override
	public List<User> findUsersByBirthdayInCurrentMonth() {
		int currentMonth = LocalDate.now().getMonthValue();

		return jpaQueryFactory.select(user)
			.from(user)
			.where(user.birthday.month().eq(currentMonth))
			.fetch();
	}
}
