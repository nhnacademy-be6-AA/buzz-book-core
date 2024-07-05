package store.buzzbook.core.service.point;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.entity.point.PointLog;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.point.PointLogRepository;
import store.buzzbook.core.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class PointService {

	private final PointLogRepository pointLogRepository;
	private final UserRepository userRepository;

	public PointLog createPointLogWithDelta(long userId, String inquiry, int deltaPoint) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			throw new UserNotFoundException(userId);
		}
		PointLog lastPointLog = pointLogRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
		PointLog newPointLog;
		if (lastPointLog == null) {
			newPointLog = PointLog.builder()
				.createdAt(LocalDateTime.now())
				.inquiry(inquiry)
				.delta(deltaPoint)
				.user(user)
				.balance(deltaPoint)
				.build();
		} else {
			newPointLog = PointLog.builder()
				.createdAt(LocalDateTime.now())
				.inquiry(inquiry)
				.delta(deltaPoint)
				.user(user)
				.balance(lastPointLog.getBalance() + deltaPoint)
				.build();
		}
		return pointLogRepository.save(newPointLog);
	}

	public PointLog createPointLogWithDelta(User user, String inquiry, int deltaPoint) {
		return createPointLogWithDelta(user.getId(), inquiry, deltaPoint);
	}
}
