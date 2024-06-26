package store.buzzbook.core.repository.user;

import static store.buzzbook.core.entity.user.QUser.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.UserStatus;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<UserInfo> findAllByBirthday(UserInfo userInfo) {

		return jpaQueryFactory
			.select(Projections.fields(UserInfo.class))
			.from(user)
			.where(
				user.birthday.month().eq(LocalDate.now().getMonthValue())
			).fetch();
	}

	@Override
	public boolean updateLoginDate(String loginId) {
		return jpaQueryFactory.update(user)
			.set(user.lastLoginAt, ZonedDateTime.now())
			.where(user.loginId.eq(loginId))
			.execute() > 0;
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
