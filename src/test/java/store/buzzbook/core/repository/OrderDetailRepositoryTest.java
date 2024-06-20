package store.buzzbook.core.repository;// package store.buzzbook.core.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import jakarta.persistence.EntityManager;
import store.buzzbook.core.CoreApplication;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.order.DeliveryPolicyRepository;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.order.WrappingRepository;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.UserRepository;

@DataJpaTest
@ContextConfiguration(classes = CoreApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderDetailRepositoryTest {
	@Autowired
	private OrderDetailRepository orderDetailRepository;

	// @Sql(scripts = "classpath:sql/OrderDetailRepositoryTest_sql.sql")
	@Test
	void findAllByOrder_Id_Test() {
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(1L);
		assertAll(
			() -> assertEquals(orderDetails.getFirst().getOrder().getId(), 1L),
			() -> assertTrue(orderDetails.getFirst().isWrap()),
			() -> assertEquals(orderDetails.getFirst().getPrice(), 10000)
		);
	}

	@Test
	void findAllByOrder_IdAndOrderStatus_Test() {
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrderStatus_Id(1L, 1);
		assertAll(
			() -> assertEquals(orderDetails.getFirst().getPrice(), 10000),
			() -> assertFalse(orderDetails.get(1).isWrap())
		);
	}
}
