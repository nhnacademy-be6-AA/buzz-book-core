package store.buzzbook.core.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import store.buzzbook.core.common.config.QuerydslConfig;
import store.buzzbook.core.entity.point.PointPolicy;
import store.buzzbook.core.repository.point.PointPolicyRepository;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@Disabled
class PointPolicyRepositoryTest {

	@Autowired
	private PointPolicyRepository pointPolicyRepository;

	private PointPolicy testPointPolicy;

	@BeforeEach
	public void setUp() {
		testPointPolicy = PointPolicy.builder()
			.name("test")
			.point(3000)
			.build();

		pointPolicyRepository.save(testPointPolicy);
	}

	@Test
	@DisplayName("find by id")
	void findById() {
		// given

		// when
		PointPolicy savedPointPolicy = pointPolicyRepository.findById(testPointPolicy.getId()).orElse(null);

		// then
		assert savedPointPolicy != null;
		assertEquals(testPointPolicy, savedPointPolicy);
	}

	@Test
	@DisplayName("save")
	void save() {
		// given
		PointPolicy newPointPolicy = PointPolicy.builder()
			.name("new")
			.rate(0.8)
			.build();

		pointPolicyRepository.save(newPointPolicy);

		// when
		Optional<PointPolicy> savedPointPolicy = pointPolicyRepository.findById(newPointPolicy.getId());

		// then
		assertTrue(savedPointPolicy.isPresent());
		assertEquals(newPointPolicy, savedPointPolicy.get());
	}

	@Test
	@DisplayName("update")
	void update() {
		// given
		String updatedName = "update";
		int updatedPoint = 5000;

		// when
		PointPolicy savedPointPolicy = pointPolicyRepository.findById(testPointPolicy.getId()).orElse(null);

		assert savedPointPolicy != null;

		PointPolicy newPointPolicy = PointPolicy.builder()
			.id(savedPointPolicy.getId())
			.point(updatedPoint)
			.name(updatedName)
			.rate(savedPointPolicy.getRate())
			.build();

		pointPolicyRepository.save(newPointPolicy);

		PointPolicy updatedPointPolicy = pointPolicyRepository.findById(testPointPolicy.getId()).orElse(null);

		// then
		assert updatedPointPolicy != null;
		assertEquals(updatedName, updatedPointPolicy.getName());
		assertEquals(updatedPoint, updatedPointPolicy.getPoint());
	}

	@Test
	@DisplayName("delete")
	void delete() {
		// given

		// when
		pointPolicyRepository.deleteById(testPointPolicy.getId());
		Optional<PointPolicy> deletedPointPolicy = pointPolicyRepository.findById(testPointPolicy.getId());

		// then
		assertFalse(deletedPointPolicy.isPresent());
	}
}
