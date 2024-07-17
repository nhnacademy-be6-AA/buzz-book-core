package store.buzzbook.core.service.point;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.point.CreatePointPolicyRequest;
import store.buzzbook.core.dto.point.DeletePointPolicyRequest;
import store.buzzbook.core.dto.point.PointLogResponse;
import store.buzzbook.core.dto.point.PointPolicyResponse;
import store.buzzbook.core.dto.point.UpdatePointPolicyRequest;
import store.buzzbook.core.entity.point.PointLog;
import store.buzzbook.core.entity.point.PointPolicy;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.point.PointLogRepository;
import store.buzzbook.core.repository.point.PointPolicyRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.point.impl.PointServiceImpl;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

	@Mock
	private PointPolicyRepository pointPolicyRepository;

	@Mock
	private PointLogRepository pointLogRepository;

	@Mock
	UserRepository userRepository;

	@InjectMocks
	private PointServiceImpl pointService;

	private PointPolicy testPointPolicy;
	private PointLog testPointLog;

	@BeforeEach
	void setUp() {
		testPointPolicy = PointPolicy.builder()
			.id(1L)
			.point(1000)
			.rate(0)
			.deleted(false)
			.build();

		testPointLog = PointLog.builder()
			.id(1L)
			.balance(1000)
			.inquiry("회원 가입")
			.delta(1000)
			.createdAt(LocalDateTime.now())
			.build();
	}

	@Test
	@DisplayName("create point policy")
	void createPointPolicy() {
		// given
		CreatePointPolicyRequest request = new CreatePointPolicyRequest("test", 1000, 0);
		when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(request.toEntity());

		// when
		PointPolicyResponse response = pointService.createPointPolicy(request);

		// then
		verify(pointPolicyRepository, times(1)).save(any(PointPolicy.class));
		assertEquals(request.name(), response.name());
		assertEquals(request.point(), response.point());
		assertEquals(request.rate(), response.rate());
	}

	@Test
	@DisplayName("get point policies")
	void getPointPolicies() {
		// given
		when(pointPolicyRepository.findAll()).thenReturn(List.of(testPointPolicy));

		// when
		List<PointPolicyResponse> policies = pointService.getPointPolicies();

		// then
		verify(pointPolicyRepository, times(1)).findAll();
		assertEquals(1, policies.size());
		assertEquals(policies.getFirst().name(), testPointPolicy.getName());
		assertEquals(policies.getFirst().point(), testPointPolicy.getPoint());
		assertEquals(policies.getFirst().rate(), testPointPolicy.getRate());
	}

	@Test
	@DisplayName("get user point")
	void getUserPoint() {
		// given
		when(pointLogRepository.existsByUserId(anyLong())).thenReturn(true);
		when(pointLogRepository.findFirstByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(testPointLog);

		// when
		int testPoint = pointService.getUserPoint(1L);

		// then
		verify(pointLogRepository, times(1)).existsByUserId(anyLong());
		verify(pointLogRepository, times(1)).findFirstByUserIdOrderByCreatedAtDesc(anyLong());
		assertEquals(testPointLog.getBalance(), testPoint);
	}

	@Test
	@DisplayName("get user point with user not exists")
	void getUserNotExists() {
		// given
		when(pointLogRepository.existsByUserId(anyLong())).thenReturn(false);

		// when
		int testPoint = pointService.getUserPoint(1L);

		// then
		verify(pointLogRepository, times(1)).existsByUserId(anyLong());
		assertEquals(0, testPoint);
	}

	@Test
	@DisplayName("update point policy")
	void updatePointPolicy() {
		// given
		UpdatePointPolicyRequest request = new UpdatePointPolicyRequest(1L, 1000, 0);
		when(pointPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testPointPolicy));

		// when
		pointService.updatePointPolicy(request);

		// then
		verify(pointPolicyRepository, times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("delete point policy")
	void deletePointPolicy() {
		// given
		DeletePointPolicyRequest request = new DeletePointPolicyRequest(1L);
		when(pointPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testPointPolicy));

		// when
		pointService.deletePointPolicy(request);

		// then
		verify(pointPolicyRepository, times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("get point logs")
	void getPointLogs() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Page<PointLog> page = new PageImpl<>(List.of(testPointLog));
		when(pointLogRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(page);

		// when
		Page<PointLogResponse> responses = pointService.getPointLogs(pageable, 1L);

		// then
		verify(pointLogRepository, times(1)).findByUserId(anyLong(), any());
		assertEquals(1, responses.getContent().size());
	}

	@Test
	@DisplayName("create point log with delta by user not found")
	void createPointLogWithDelta_UserNotFound() {
		// given
		long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> pointService.createPointLogWithDelta(userId, "test", 10))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("create point log with delta by no previous logs")
	void testCreatePointLogWithDelta_NoPreviousLogs() {
		// given
		long userId = 1L;
		User user = User.builder().id(userId).name("test").build();
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(pointLogRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)).thenReturn(null);
		when(pointLogRepository.save(any(PointLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		PointLog result = pointService.createPointLogWithDelta(userId, "test", 10);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getUser()).isEqualTo(user);
		assertThat(result.getDelta()).isEqualTo(10);
		assertThat(result.getBalance()).isEqualTo(10);
	}

	@Test
	void testCreatePointLogWithDelta_PreviousLogsExist() {
		// given
		long userId = 1L;
		User user = User.builder().id(userId).name("test").build();
		PointLog lastPointLog = PointLog.builder()
			.user(user)
			.createdAt(LocalDateTime.now().minusDays(1))
			.inquiry("test")
			.delta(20)
			.balance(20)
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(pointLogRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)).thenReturn(lastPointLog);
		when(pointLogRepository.save(any(PointLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		PointLog result = pointService.createPointLogWithDelta(user, "test", 10);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getUser()).isEqualTo(user);
		assertThat(result.getDelta()).isEqualTo(10);
		assertThat(result.getBalance()).isEqualTo(30);
	}

}
