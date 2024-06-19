package store.buzzbook.core.repository;// package store.buzzbook.core.repository;
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
//
// import store.buzzbook.core.CoreApplication;
// import store.buzzbook.core.entity.order.DeliveryPolicy;
// import store.buzzbook.core.entity.order.Order;
// import store.buzzbook.core.entity.order.OrderDetail;
// import store.buzzbook.core.entity.order.OrderStatus;
// import store.buzzbook.core.entity.order.Wrapping;
// import store.buzzbook.core.entity.product.Category;
// import store.buzzbook.core.entity.product.Product;
// import store.buzzbook.core.entity.user.User;
// import store.buzzbook.core.repository.order.DeliveryPolicyRepository;
// import store.buzzbook.core.repository.order.OrderDetailRepository;
// import store.buzzbook.core.repository.order.OrderRepository;
// import store.buzzbook.core.repository.order.OrderStatusRepository;
// import store.buzzbook.core.repository.order.WrappingRepository;
// import store.buzzbook.core.repository.product.CategoryRepository;
// import store.buzzbook.core.repository.product.ProductRepository;
// import store.buzzbook.core.repository.user.UserRepository;
//
// @DataJpaTest
// @ContextConfiguration(classes = CoreApplication.class)
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// class OrderDetailRepositoryTest {
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
// 	private OrderDetail orderDetail1;
// 	private OrderDetail orderDetail2;
// 	private OrderStatus orderStatus;
// 	private Product product1;
// 	private Product product2;
// 	private Wrapping wrapping;
// 	private Category category;
// 	@Autowired
// 	private CategoryRepository categoryRepository;
//
// 	@BeforeEach
// 	public void setUp() {
// 		user = User.builder().build();
// 		category = Category.builder().name("IT").build();
// 		product1 = Product.builder().category(category).score(1.1).stock(1).price(new BigDecimal("1000")).build();
// 		product2 = Product.builder().category(category).score(1.1).stock(1).price(new BigDecimal("2000")).build();
// 		deliveryPolicy = DeliveryPolicy.builder()
// 			.name("정책1")
// 			.standardPrice(1000)
// 			.policyPrice(1000)
// 			.build();
// 		order = Order.builder()
// 			.address("광주")
// 			.addressDetail("조선대학교")
// 			.price(1000)
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
// 			.price(1000)
// 			.paper("신문지")
// 			.build();
//
// 		orderDetail1 = OrderDetail.builder()
// 			.orderStatus(orderStatus)
// 			.order(order)
// 			.wrap(true)
// 			.createdDate(ZonedDateTime.now())
// 			.price(10000)
// 			.product(product1)
// 			.quantity(1)
// 			.wrapping(wrapping)
// 			.build();
// 		orderDetail2 = OrderDetail.builder()
// 			.orderStatus(orderStatus)
// 			.order(order)
// 			.wrap(false)
// 			.createdDate(ZonedDateTime.now())
// 			.price(2000)
// 			.product(product2)
// 			.quantity(1)
// 			.wrapping(wrapping)
// 			.build();
//
// 		categoryRepository.save(category);
// 		wrappingRepository.save(wrapping);
// 		orderStatusRepository.save(orderStatus);
// 		userRepository.save(user);
// 		productRepository.save(product1);
// 		productRepository.save(product2);
// 		deliveryPolicyRepository.save(deliveryPolicy);
// 		orderRepository.save(order);
// 		orderDetailRepository.save(orderDetail1);
// 		orderDetailRepository.save(orderDetail2);
// 	}
//
// 	@Test
// 	void findAllByOrder_Id_Test() {
// 		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
// 		assertAll(
// 			() -> assertEquals(orderDetails.getFirst().getOrder().getId(), order.getId()),
// 			() -> assertTrue(orderDetails.getFirst().isWrap()),
// 			() -> assertEquals(orderDetails.getFirst().getPrice(), 10000)
// 		);
// 	}
//
// 	@Test
// 	void findAllByOrder_IdAndOrderStatus_Test() {
// 		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrderStatus(1L, orderStatus);
// 		assertAll(
// 			() -> assertEquals(orderDetails.getFirst().getPrice(), 10000),
// 			() -> assertFalse(orderDetails.get(1).isWrap())
// 		);
// 	}
// }
