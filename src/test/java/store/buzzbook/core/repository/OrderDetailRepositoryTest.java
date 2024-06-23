package store.buzzbook.core.repository;// package store.buzzbook.core.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import store.buzzbook.core.CoreApplication;

@DataJpaTest
@ContextConfiguration(classes = CoreApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderDetailRepositoryTest {
	// @Autowired
	// private OrderDetailRepository orderDetailRepository;
	//
	// // @Sql(scripts = "classpath:sql/OrderDetailRepositoryTest_sql.sql")
	// @Test
	// void findAllByOrder_Id_Test() {
	// 	List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(1L);
	// 	assertAll(
	// 		() -> assertEquals(orderDetails.getFirst().getOrder().getId(), 1L),
	// 		() -> assertTrue(orderDetails.getFirst().isWrap()),
	// 		() -> assertEquals(orderDetails.getFirst().getPrice(), 10000)
	// 	);
	// }
	//
	// @Test
	// void findAllByOrder_IdAndOrderStatus_Test() {
	// 	List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrderStatus_Id(1L, 1);
	// 	assertAll(
	// 		() -> assertEquals(orderDetails.getFirst().getPrice(), 10000),
	// 		() -> assertFalse(orderDetails.get(1).isWrap())
	// 	);
	// }
}
