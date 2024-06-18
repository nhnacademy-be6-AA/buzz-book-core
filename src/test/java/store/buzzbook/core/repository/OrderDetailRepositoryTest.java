// package store.buzzbook.core.repository;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.math.BigDecimal;
// import java.time.ZonedDateTime;
// import java.util.List;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.context.jdbc.Sql;
//
// import store.buzzbook.core.CoreApplication;
// import store.buzzbook.core.entity.order.DeliveryPolicy;
// import store.buzzbook.core.entity.order.Order;
// import store.buzzbook.core.entity.order.OrderDetail;
// import store.buzzbook.core.entity.order.OrderStatus;
// import store.buzzbook.core.entity.order.Wrapping;
// import store.buzzbook.core.entity.product.Product;
// import store.buzzbook.core.entity.user.User;
// import store.buzzbook.core.repository.order.DeliveryPolicyRepository;
// import store.buzzbook.core.repository.order.OrderDetailRepository;
// import store.buzzbook.core.repository.order.OrderRepository;
// import store.buzzbook.core.repository.order.OrderStatusRepository;
// import store.buzzbook.core.repository.order.WrappingRepository;
//
// @DataJpaTest
// @ContextConfiguration(classes = CoreApplication.class)
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// public class OrderDetailRepositoryTest {
// 	@Autowired
// 	private OrderRepository orderRepository;
// 	@Autowired
// 	private UserRepository userRepository;
// 	@Autowired
// 	private DeliveryPolicyRepository deliveryPolicyRepository;
// 	@Autowired
// 	private OrderStatusRepository orderStatusRepository;
// 	@Autowired
// 	private ProductRepository productRepository;
// 	@Autowired
// 	private WrappingRepository wrappingRepository;
// 	@Autowired
// 	private OrderDetailRepository orderDetailRepository;
//
// 	private Order order;
// 	private User user;
// 	private DeliveryPolicy deliveryPolicy;
// 	private OrderDetail orderDetail;
// 	private OrderStatus orderStatus;
// 	private Product product;
// 	private Wrapping wrapping;
//
// 	@BeforeEach
// 	public void setUp() {
// 		user = User.builder().build();
// 		product = Product.builder().build();
// 		deliveryPolicy = DeliveryPolicy.builder()
// 			.name("정책1")
// 			.standardPrice(new BigDecimal("1000"))
// 			.policyPrice(new BigDecimal("1000"))
// 			.build();
// 		order = Order.builder()
// 			.address("광주")
// 			.addressDetail("조선대학교")
// 			.price(new BigDecimal("1000"))
// 			.deliveryPolicy(deliveryPolicy)
// 			.receiver("PS")
// 			.request("감사합니다.")
// 			.zipcode(00000)
// 			.user(user)
// 			.build();
// 		orderStatus = OrderStatus.builder()
// 			.updateDate(ZonedDateTime.now())
// 			.name("환불")
// 			.build();
// 		wrapping = Wrapping.builder()
// 			.price(new BigDecimal("1000"))
// 			.paper("신문지")
// 			.build();
//
// 		orderDetail = OrderDetail.builder()
// 			.orderStatus(orderStatus)
// 			.order(order)
// 			.wrap(true)
// 			.createdDate(ZonedDateTime.now())
// 			.price(new BigDecimal("10000"))
// 			.product(product)
// 			.quantity(1)
// 			.wrapping(wrapping)
// 			.build();
//
// 		wrappingRepository.save(wrapping);
// 		orderStatusRepository.save(orderStatus);
// 		userRepository.save(user);
// 		productRepository.save(product);
// 		deliveryPolicyRepository.save(deliveryPolicy);
// 		orderRepository.save(order);
// 		orderDetailRepository.save(orderDetail);
// 	}
//
// 	@Test
// 	@Sql(scripts = "classpath:sql/OrderDetail-test.sql")
// 	public void findAllByOrder_Id_Test() {
// 		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
// 		assertAll(
// 			() -> assertEquals(orderDetails.getFirst().getOrder().getId(), order.getId()),
// 			() -> assertTrue(orderDetails.getFirst().isWrap()),
// 			() -> assertEquals(orderDetails.getFirst().getPrice().intValue(), 10000)
// 		);
// 	}
// }
