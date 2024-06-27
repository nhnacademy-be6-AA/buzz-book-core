package store.buzzbook.core.repository.user;

import static store.buzzbook.core.entity.user.QGrade.*;
import static store.buzzbook.core.entity.user.QGradeLog.*;
import static store.buzzbook.core.entity.user.QUser.*;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.entity.user.Grade;

@RequiredArgsConstructor
@Transactional
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
			.orderBy(gradeLog.changeAt.asc())
			.fetchFirst();

		return Optional.ofNullable(targetGrade);
	}
}
