package store.buzzbook.core.repository.user;

import static store.buzzbook.core.entity.user.QGrade.*;
import static store.buzzbook.core.entity.user.QGradeLog.*;
import static store.buzzbook.core.entity.user.QUser.*;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.UserStatus;

@RequiredArgsConstructor
@Transactional
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<Grade> findGradeByUserId(Long userId) {
		Grade resultGrade = null;
		resultGrade = jpaQueryFactory.select(Projections.fields(Grade.class))
			.from(user)
			.where(user.id.eq(userId))
			.innerJoin(gradeLog)
			.on(user.id.eq(gradeLog.user.id))
			.innerJoin(grade)
			.on(grade.id.eq(gradeLog.grade.id))
			.fetchFirst();

		return Optional.ofNullable(resultGrade);
	}

	@Override
	public boolean updateStatus(Long userId, UserStatus status) {
		return jpaQueryFactory.update(user)
			.set(user.status, status)
			.where(user.id.eq(userId))
			.execute() > 0;
	}

	@Override
	public boolean updateStatus(String loginId, UserStatus status) {
		return jpaQueryFactory.update(user)
			.set(user.status, status)
			.where(user.loginId.eq(loginId))
			.execute() > 0;
	}

}
