package store.buzzbook.core.repository.user;

import static store.buzzbook.core.entity.order.QOrder.*;
import static store.buzzbook.core.entity.payment.QBillLog.*;
import static store.buzzbook.core.entity.user.QGrade.*;
import static store.buzzbook.core.entity.user.QGradeLog.*;
import static store.buzzbook.core.entity.user.QUser.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.user.UserRealBill;
import store.buzzbook.core.dto.user.UserRealBillInfo;
import store.buzzbook.core.dto.user.UserRealBillInfoDetail;
import store.buzzbook.core.entity.payment.BillStatus;
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

	@Override
	public List<UserRealBill> findUserRealBillsIn3Month() {
		LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

		List<Tuple> results = jpaQueryFactory.select(
				user.id,
				order.deliveryRate,
				billLog.status,
				billLog.price
			)
			.from(billLog)
			.leftJoin(order)
			.on(billLog.order.id.eq(order.id)
				.and(order.isNotNull()
					.and(billLog.status.in(BillStatus.DONE, BillStatus.CANCELED, BillStatus.REFUND,
						BillStatus.CANCELED).and(billLog.payment.in("POINT", "간편결제")))))
			.innerJoin(user)
			.on(user.id.eq(order.user.id))
			.where(billLog.payAt.after(threeMonthsAgo))
			.fetch();

		Map<Long, UserRealBill> userRealBillMap = new HashMap<>();

		for (Tuple tuple : results) {
			Long userId = tuple.get(user.id);
			int deliveryRate = Objects.requireNonNull(tuple.get(order.deliveryRate));
			BillStatus status = tuple.get(billLog.status);
			int price = Objects.requireNonNull(tuple.get(billLog.price));

			UserRealBill userRealBill = userRealBillMap.computeIfAbsent(userId, id ->
				UserRealBill.builder()
					.userId(userId)
					.userRealBillInfoList(new LinkedList<>())
					.build()
			);

			UserRealBillInfoDetail detail = UserRealBillInfoDetail.builder()
				.status(status)
				.price(price)
				.build();

			UserRealBillInfo billInfo = userRealBill.getUserRealBillInfoList().stream()
				.filter(info -> info.getDeliveryRate() == deliveryRate)
				.findFirst()
				.orElse(null);

			if (billInfo == null) {
				billInfo = UserRealBillInfo.builder()
					.deliveryRate(deliveryRate)
					.detailList(new LinkedList<>())
					.build();
				userRealBill.getUserRealBillInfoList().add(billInfo);
			}

			billInfo.getDetailList().add(detail);
		}

		return new LinkedList<>(userRealBillMap.values());
	}
}
