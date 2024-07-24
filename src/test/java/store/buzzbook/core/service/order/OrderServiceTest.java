package store.buzzbook.core.service.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;

import store.buzzbook.core.dto.order.CreateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.CreateOrderDetailRequest;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.CreateWrappingRequest;
import store.buzzbook.core.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.ReadOrderStatusResponse;
import store.buzzbook.core.dto.order.ReadOrderWithoutLoginRequest;
import store.buzzbook.core.dto.order.ReadOrdersRequest;
import store.buzzbook.core.dto.order.ReadOrdersResponse;
import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.core.dto.order.UpdateOrderRequest;
import store.buzzbook.core.dto.product.CategoryResponse;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.product.TagResponse;
import store.buzzbook.core.dto.user.GradeInfoResponse;
import store.buzzbook.core.dto.user.UserInfo;
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
import store.buzzbook.core.repository.point.PointPolicyRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.AddressRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.point.PointService;
import store.buzzbook.core.service.user.UserService;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderDetailRepository orderDetailRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private DeliveryPolicyRepository deliveryPolicyRepository;

	@Mock
	private WrappingRepository wrappingRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private OrderStatusRepository orderStatusRepository;

	@Mock
	private UserService userService;

	@Mock
	private AddressRepository addressRepository;

	@Mock
	private PointPolicyRepository pointPolicyRepository;

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private PointService pointService;

	@InjectMocks
	private OrderService orderService;

	private List<CreateOrderDetailRequest> createOrderDetailRequests;
	private CreateOrderDetailRequest createOrderDetailRequest1;
	private CreateOrderDetailRequest createOrderDetailRequest2;
	private CreateOrderRequest createOrderRequest;
	private Order order;
	private User testUser;
	private List<OrderDetail> details = new ArrayList<>();
	private OrderDetail orderDetail;
	private OrderStatus orderStatus;
	private Wrapping wrapping;
	private Wrapping wrapping2;
	private Product product;
	private Category category;
	private UserInfo testUserInfo;
	private ReadOrderDetailResponse readOrderDetailResponse1;
	private ReadOrderDetailResponse readOrderDetailResponse2;
	private List<ReadOrderDetailResponse> readOrderDetailResponses;
	private ReadOrderStatusResponse readOrderStatusResponse;
	private ReadWrappingResponse readWrappingResponse1;
	private ReadWrappingResponse readWrappingResponse2;
	private ProductResponse productResponse1;
	private ProductResponse productResponse2;
	private CategoryResponse categoryResponse;
	private LinkedHashMap<Integer, String> parentCategory = new LinkedHashMap<>();
	private List<TagResponse> tags = new ArrayList<>();
	private TagResponse tagResponse;
	private ReadOrderResponse readOrderResponse;

	@BeforeEach
	void setUp() {
		applicationContext.getBean(OrderService.class);

		testUserInfo = new UserInfo(504L, "parkseol", "01011111111", "parkseol",
			"parkseol.dev@gmail.com", LocalDate.parse("2024-06-28"), GradeInfoResponse.builder().benefit(0.03)
			.name("PLATINUM").standard(300000).build(), true, 5000);

		testUser = User.builder()
			.id(504L)
			.name("parkseol")
			.build();

		orderStatus = OrderStatus.builder().updateAt(LocalDateTime.now()).name("PAID").id(4).build();

		wrapping = Wrapping.builder().deleted(false).paper("선물포장").price(1000).id(1).build();
		wrapping2 = Wrapping.builder().deleted(false).paper("신문지").price(100).id(2).build();

		category = Category.builder()
			.id(1)
			.name("Existing Category")
			.subCategories(new ArrayList<>())
			.build();

		product = Product.builder()
			.stock(10)
			.productName("갤럭시탭 S8")
			.description("감성대신 실력을 더했다")
			.price(87500)
			.forwardDate(LocalDate.now())
			.score(5)
			.thumbnailPath("path/to/thumbnail")
			.stockStatus(Product.StockStatus.SALE)
			.category(category)
			.build();

		orderDetail = OrderDetail.builder().wrapping(wrapping)
			.wrap(true).order(order).orderStatus(orderStatus)
			.order(order).quantity(3).price(2000).createAt(LocalDateTime.now())
			.updateAt(LocalDateTime.now()).product(product).id(1L).build();

		details.add(orderDetail);

		order = Order.builder().id(1L).orderEmail("john.doe@example.com").orderStr("orderStr123").couponCode("DISCOUNT2024")
			.details(details).addressDetail("Apt 4B").sender("Jane Smith").address("123 Main St, Cityville")
			.zipcode(12345).senderContactNumber("01012345678").receiverContactNumber("01087654321")
			.price(3000).user(testUser).request("Please deliver between 9 AM and 5 PM")
			.deliveryRate(5000).desiredDeliveryDate(LocalDate.parse("2024-08-15")).build();

		createOrderDetailRequest1 = new CreateOrderDetailRequest(
			1000,
			2,
			true,
			LocalDateTime.now(),
			1,
			10,
			12345L,
			100,
			"Product 1",
			"/images/product1.png",
			"COUPON123"
		);

		createOrderDetailRequest2 = new CreateOrderDetailRequest(
			2000,
			1,
			false,
			LocalDateTime.now(),
			2,
			11,
			12346L,
			101,
			"Product 2",
			"/images/product2.png",
			"COUPON124"
		);

		createOrderDetailRequests = Arrays.asList(createOrderDetailRequest1, createOrderDetailRequest2);

		createOrderRequest = new CreateOrderRequest(
			"orderStr123",
			1500,
			"Please deliver between 9 AM and 5 PM",
			"123 Main St, Cityville",
			"123 Main St",
			"Apt 4B",
			12345,
			"2024-08-15",
			"John Doe",
			1,
			"john.doe",
			createOrderDetailRequests,
			"01012345678",
			2,
			"Jane Smith",
			"01087654321",
			"john.doe@example.com",
			100,
			"DISCOUNT2024",
			50
		);

		readOrderStatusResponse = new ReadOrderStatusResponse(4, "PAID", "2024-07-15 12:01:11");
		readWrappingResponse1 = new ReadWrappingResponse(1, "선물포장", 1000, false);
		readWrappingResponse2 = new ReadWrappingResponse(2, "신문지", 0, false);

		parentCategory = new LinkedHashMap<>();
		parentCategory.put(0, "전체");
		categoryResponse = new CategoryResponse(1, "국내도서", parentCategory, null);
		tagResponse = new TagResponse(1, "포장가능");
		tags.add(0, tagResponse);
		productResponse1 = new ProductResponse(1, 10, "리눅스 구조", "description",
			5000, LocalDate.now(), 3, "/images/productA.jpg", Product.StockStatus.SALE,
			categoryResponse, tags);
		productResponse2 = new ProductResponse(2, 10, "알고리즘 코딩테스트", "description",
			5000, LocalDate.now(), 3, "/images/productA.jpg", Product.StockStatus.SALE,
			categoryResponse, tags);

		readOrderDetailResponse1 = new ReadOrderDetailResponse(
			1L,                            // id
			5000,                           // price
			2,                             // quantity
			true,                          // wrap
			"2024-07-10T14:48:00Z",        // createdAt
			readOrderStatusResponse,           // readOrderStatusResponse
			readWrappingResponse1,              // readWrappingResponse
			productResponse1,               // productResponse
			"2024-07-12T16:00:00Z"         // updatedAt
		);

		readOrderDetailResponse2 = new ReadOrderDetailResponse(
			1L,                            // id
			5000,                           // price
			2,                             // quantity
			true,                          // wrap
			"2024-07-10T14:48:00Z",        // createdAt
			readOrderStatusResponse,           // readOrderStatusResponse
			readWrappingResponse2,              // readWrappingResponse
			productResponse2,               // productResponse
			"2024-07-12T16:00:00Z"         // updatedAt
		);

		readOrderDetailResponses = Arrays.asList(readOrderDetailResponse1, readOrderDetailResponse2);

		readOrderResponse = new ReadOrderResponse(
			1L,                    // id
			"MC4wOTA4MjAzNjg2OTQy",                 // orderStr
			"testUser",              // loginId
			15500,                        // price
			"Please deliver quickly.",  // request
			"123 Main St",              // address
			"Apt 4B",                   // addressDetail
			12345,                      // zipcode
			"2024-07-15",               // desiredDeliveryDate
			"John Doe",                 // receiver
			readOrderDetailResponses,                    // details
			"Jane Smith",               // sender
			"098-765-4321",                 // receiverContactNumber
			"123-456-7890",                 // senderContactNumber
			"parkseol.dev@gmail.com",   // orderEmail
			null,                   // couponCode
			0                          // deliveryRate
		);
	}

	// @Disabled
	// @Test
	// void testGetOrders() {
	// 	ReadOrdersRequest request = new ReadOrdersRequest(1, 10);
	// 	List<ReadOrderProjectionResponse> orders = new ArrayList<>();
	//
	// 	when(orderRepository.findAll(any(ReadOrdersRequest.class))).thenReturn(orders);
	//
	// 	List<ReadOrderProjectionResponse> response = orderService.getOrders(request);
	// 	assertNotNull(response);
	// }
	//
	// @Disabled
	// @Test
	// void testReadOrders() {
	// 	ReadOrdersRequest request = new ReadOrdersRequest(1, 10);
	// 	List<ReadOrderProjectionResponse> orders = new ArrayList<>();
	//
	// 	when(orderRepository.findAll(any(ReadOrdersRequest.class))).thenReturn(orders);
	//
	// 	Map<String, Object> data = orderService.readOrders(request);
	// 	assertNotNull(data);
	// 	assertTrue(data.containsKey("responseData"));
	// 	assertTrue(data.containsKey("total"));
	// }
	//
	// @Disabled
	// @Test
	// void testReadMyOrders() {
	// 	ReadOrdersRequest request = new ReadOrdersRequest(1, 10);
	// 	String loginId = "testUser";
	// 	List<ReadOrderProjectionResponse> orders = new ArrayList<>();
	//
	// 	when(orderRepository.findAllByUser_LoginId(any(ReadOrdersRequest.class), eq(loginId))).thenReturn(orders);
	//
	// 	Map<String, Object> data = orderService.readMyOrders(request, loginId);
	// 	assertNotNull(data);
	// 	assertTrue(data.containsKey("responseData"));
	// 	assertTrue(data.containsKey("total"));
	// }

	@Disabled
	@Test
	void testCreateOrder() {
		CreateOrderDetailRequest detail1 = CreateOrderDetailRequest.builder()
			.price(2000)
			.quantity(3)
			.wrap(true)
			.createAt(LocalDateTime.now())
			.orderStatusId(4)
			.wrappingId(2)
			.orderId(1L)
			.productId(101)
			.productName("Sample Product")
			.thumbnailPath("/images/sample-product.png")
			.couponCode("SUMMER2024")
			.build();

		CreateOrderDetailRequest detail2 = CreateOrderDetailRequest.builder()
			.price(1500)
			.quantity(2)
			.wrap(false)
			.createAt(LocalDateTime.now())
			.orderStatusId(4)
			.wrappingId(1)
			.orderId(1L)
			.productId(102)
			.productName("Another Product")
			.thumbnailPath("/images/another-product.png")
			.build();

		List<CreateOrderDetailRequest> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		CreateOrderRequest createOrderRequest1 = CreateOrderRequest.builder()
			.orderStr("orderStr123")
			.price(3500)
			.request("Please deliver between 9 AM and 5 PM")
			.addresses("123 Main St, Cityville")
			.address("123 Main St")
			.addressDetail("Apt 4B")
			.zipcode(12345)
			.desiredDeliveryDate("2024-08-15")
			.receiver("John Doe")
			.deliveryPolicyId(1)
			.loginId("john.doe")
			.details(details)
			.contactNumber("01012345678")
			.orderStatusId(1)
			.sender("Jane Smith")
			.receiverContactNumber("01087654321")
			.orderEmail("john.doe@example.com")
			.myPoint(5000)
			.couponCode("DISCOUNT2024")
			.deliveryRate(50)
			.build();

		when(userService.getUserInfoByLoginId(createOrderRequest1.getLoginId())).thenReturn(testUserInfo);
		when(userRepository.findById(testUser.getId())).thenReturn(Optional.ofNullable(testUser));
		when(orderRepository.save(any(Order.class))).thenReturn(order);
		when(orderService.createOrder(createOrderRequest1)).thenReturn(readOrderResponse);

		orderService.createOrder(createOrderRequest1);

		verify(userRepository, times(1)).findById(anyLong());
		verify(orderRepository, times(1)).save(any(Order.class));
	}

	// @Test
	// void testUpdateOrderWithAdmin() {
	// 	UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest(); // Example request object
	// 	Order order = new Order(); // Mock data
	// 	List<OrderDetail> orderDetails = new ArrayList<>(); // Mock data
	//
	// 	when(orderRepository.findByOrderStr(anyString())).thenReturn(order);
	// 	when(orderDetailRepository.findAllByOrder_Id(anyLong())).thenReturn(orderDetails);
	// 	when(orderStatusRepository.findByName(anyString())).thenReturn(new OrderStatus());
	//
	// 	ReadOrderResponse response = orderService.updateOrderWithAdmin(updateOrderRequest);
	// 	assertNotNull(response);
	// }
	//
	// @Test
	// void testUpdateOrder() {
	// 	UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest(); // Example request object
	// 	String loginId = "testUser";
	// 	Order order = new Order(); // Mock data
	// 	List<OrderDetail> orderDetails = new ArrayList<>(); // Mock data
	//
	// 	when(orderRepository.findByOrderStr(anyString())).thenReturn(order);
	// 	when(orderDetailRepository.findAllByOrder_IdAndOrder_User_LoginId(anyLong(), eq(loginId))).thenReturn(orderDetails);
	// 	when(orderStatusRepository.findByName(anyString())).thenReturn(new OrderStatus());
	//
	// 	ReadOrderResponse response = orderService.updateOrder(updateOrderRequest, loginId);
	// 	assertNotNull(response);
	// }
	//
	// @Test
	// void testReadOrder() {
	// 	Long orderId = 1L;
	// 	String orderStr = "orderStr";
	// 	String orderEmail = "email@example.com";
	// 	String loginId =  "testUser";
	//
	// 	Order order = Order.builder().id(orderId).orderEmail(orderEmail).build();
	//
	// 	OrderDetail orderDetail = OrderDetail.builder().order(order).build();
	//
	// 	when(orderRepository.findByOrderStr(orderStr)).thenReturn(order);
	// 	when(orderDetailRepository.findAllByOrder_Id(order.getId())).thenReturn(List.of(orderDetail));
	// 	when(productRepository.findById(anyInt())).thenReturn(Optional.of(new Product()));
	// 	when(wrappingRepository.findById(anyInt())).thenReturn(Optional.of(new Wrapping()));
	//
	// 	ReadOrderResponse response = orderService.readOrder(new ReadOrderRequest(orderStr), loginId);
	//
	//
	// 	assertNotNull(response);
	// 	assertEquals(orderId, response.getId());
	// }
	//
	// @Test
	// void testReadOrderWithoutLogin() {
	// 	Long orderId = 1L;
	// 	String orderStr = "orderStr";
	// 	String orderEmail = "email@example.com";
	//
	// 	Order order = Order.builder().id(orderId).orderEmail(orderEmail).build();
	//
	// 	OrderDetail orderDetail = OrderDetail.builder().order(order).build();
	//
	// 	when(orderRepository.findByOrderStr(orderStr)).thenReturn(order);
	// 	when(orderDetailRepository.findAllByOrder_IdAndOrder_OrderEmail(order.getId(), orderEmail)).thenReturn(List.of(orderDetail));
	// 	when(productRepository.findById(anyInt())).thenReturn(Optional.of(new Product()));
	// 	when(wrappingRepository.findById(anyInt())).thenReturn(Optional.of(new Wrapping()));
	//
	// 	ReadOrderResponse response = orderService.readOrderWithoutLogin(new ReadOrderWithoutLoginRequest(orderStr, orderEmail));
	//
	// 	assertNotNull(response);
	// 	assertEquals(orderId, response.getId());
	// }
	//
	// @Test
	// void testReadOrderStatusById() {
	// 	int id = 1;
	// 	OrderStatus orderStatus = OrderStatus.builder().id(id).build();
	//
	// 	when(orderStatusRepository.findById(id)).thenReturn(Optional.of(orderStatus));
	//
	// 	ReadOrderStatusResponse response = orderService.readOrderStatusById(id);
	//
	// 	assertNotNull(response);
	// 	assertEquals(id, response.getId());
	// }
	//
	// @Test
	// void testReadOrderStatusByName() {
	// 	String name = "CANCELED";
	// 	OrderStatus orderStatus = OrderStatus.builder().name("CANCELED").build();
	//
	// 	when(orderStatusRepository.findByName(name)).thenReturn(orderStatus);
	//
	// 	ReadOrderStatusResponse response = orderService.readOrderStatusByName(name);
	//
	// 	assertNotNull(response);
	// 	assertEquals(name, response.getName());
	// }
	//
	// @Test
	// void testReadAllOrderStatus() {
	// 	OrderStatus orderStatus = OrderStatus.builder().id(1).name("CANCELED").updateAt(LocalDateTime.now()).build();
	// 	List<OrderStatus> orderStatusList = List.of(orderStatus);
	//
	// 	when(orderStatusRepository.findAll()).thenReturn(orderStatusList);
	//
	// 	List<ReadOrderStatusResponse> responseList = orderService.readAllOrderStatus();
	//
	// 	assertNotNull(responseList);
	// 	assertFalse(responseList.isEmpty());
	// }
	//
	// @Test
	// void testCreateDeliveryPolicy() {
	// 	CreateDeliveryPolicyRequest request = new CreateDeliveryPolicyRequest();
	//
	// 	DeliveryPolicy deliveryPolicy = DeliveryPolicy.builder().name("기본").policyPrice(0).standardPrice(20000).build();
	//
	// 	when(deliveryPolicyRepository.save(any(DeliveryPolicy.class))).thenReturn(deliveryPolicy);
	//
	// 	ReadDeliveryPolicyResponse response = orderService.createDeliveryPolicy(request);
	//
	// 	assertNotNull(response);
	// 	assertEquals("policyName", response.getName());
	// }
	//
	// @Test
	// void testDeleteDeliveryPolicy() {
	// 	int id = 1;
	// 	DeliveryPolicy deliveryPolicy = new DeliveryPolicy();
	//
	// 	when(deliveryPolicyRepository.findById(id)).thenReturn(Optional.of(deliveryPolicy));
	//
	// 	orderService.deleteDeliveryPolicy(id);
	//
	// 	verify(deliveryPolicyRepository, times(1)).save(deliveryPolicy);
	// 	assertTrue(deliveryPolicy.isDeleted());
	// }
	//
	// @Test
	// void testReadDeliveryPolicyById() {
	// 	int id = 1;
	// 	DeliveryPolicy deliveryPolicy = DeliveryPolicy.builder().id(id).build();
	//
	// 	when(deliveryPolicyRepository.findById(id)).thenReturn(Optional.of(deliveryPolicy));
	//
	// 	ReadDeliveryPolicyResponse response = orderService.readDeliveryPolicyById(id);
	//
	// 	assertNotNull(response);
	// 	assertEquals(id, response.getId());
	// }
	//
	// @Test
	// void testReadAllDeliveryPolicy() {
	// 	DeliveryPolicy deliveryPolicy = new DeliveryPolicy();
	// 	List<DeliveryPolicy> deliveryPolicyList = List.of(deliveryPolicy);
	//
	// 	when(deliveryPolicyRepository.findAll()).thenReturn(deliveryPolicyList);
	//
	// 	List<ReadDeliveryPolicyResponse> responseList = orderService.readAllDeliveryPolicy();
	//
	// 	assertNotNull(responseList);
	// 	assertFalse(responseList.isEmpty());
	// }
	//
	// @Test
	// void testCreateWrapping() {
	// 	CreateWrappingRequest request = new CreateWrappingRequest("신문지", 1000);
	//
	// 	Wrapping wrapping = Wrapping.builder().paper("신문지").price(1000).build();
	//
	// 	when(wrappingRepository.save(any(Wrapping.class))).thenReturn(wrapping);
	//
	// 	ReadWrappingResponse response = orderService.createWrapping(request);
	//
	// 	assertNotNull(response);
	// 	assertEquals("paperType", response.getPaper());
	// }
	//
	// @Test
	// void testDeleteWrapping() {
	// 	int id = 1;
	// 	Wrapping wrapping = new Wrapping();
	//
	// 	when(wrappingRepository.findById(id)).thenReturn(Optional.of(wrapping));
	//
	// 	orderService.deleteWrapping(id);
	//
	// 	verify(wrappingRepository, times(1)).save(wrapping);
	// 	assertTrue(wrapping.isDeleted());
	// }
	//
	// @Test
	// void testReadWrappingById() {
	// 	int id = 1;
	// 	Wrapping wrapping = Wrapping.builder().id(id).build();
	//
	// 	when(wrappingRepository.findById(id)).thenReturn(Optional.of(wrapping));
	//
	// 	ReadWrappingResponse response = orderService.readWrappingById(id);
	//
	// 	assertNotNull(response);
	// 	assertEquals(id, response.getId());
	// }
	//
	// @Test
	// void testReadAllWrapping() {
	// 	Wrapping wrapping = new Wrapping();
	// 	List<Wrapping> wrappingList = List.of(wrapping);
	//
	// 	when(wrappingRepository.findAll()).thenReturn(wrappingList);
	//
	// 	List<ReadWrappingResponse> responseList = orderService.readAllWrapping();
	//
	// 	assertNotNull(responseList);
	// 	assertFalse(responseList.isEmpty());
	// }
	//
	// @Test
	// void testUpdateOrderDetail() {
	// 	String loginId = "loginId";
	// 	UpdateOrderDetailRequest request = new UpdateOrderDetailRequest(1L, "CANCELED");
	//
	// 	OrderDetail orderDetail = OrderDetail.builder().id(1L).product(new Product()).wrapping(new Wrapping()).build();
	//
	// 	when(orderDetailRepository.findByIdAndOrder_User_LoginId(request.getId(), loginId)).thenReturn(orderDetail);
	// 	when(productRepository.findById(anyInt())).thenReturn(Optional.of(new Product()));
	// 	when(wrappingRepository.findById(anyInt())).thenReturn(Optional.of(new Wrapping()));
	//
	// 	ReadOrderDetailResponse response = orderService.updateOrderDetail(request, loginId);
	//
	// 	assertNotNull(response);
	// 	assertEquals(request.getId(), response.getId());
	// }
	//
	// @Test
	// void testUpdateOrderDetailWithAdmin() {
	// 	UpdateOrderDetailRequest request = new UpdateOrderDetailRequest(1L, "CANCELED");
	//
	// 	OrderDetail orderDetail = OrderDetail.builder().id(1L).product(new Product()).wrapping(new Wrapping()).build();
	//
	// 	when(orderDetailRepository.findById(request.getId())).thenReturn(Optional.of(orderDetail));
	// 	when(productRepository.findById(anyInt())).thenReturn(Optional.of(new Product()));
	// 	when(wrappingRepository.findById(anyInt())).thenReturn(Optional.of(new Wrapping()));
	//
	// 	ReadOrderDetailResponse response = orderService.updateOrderDetailWithAdmin(request);
	//
	// 	assertNotNull(response);
	// 	assertEquals(request.getId(), response.getId());
	// }
	//
	// @Test
	// void testReadOrderStr() {
	// 	long orderDetailId = 1L;
	// 	String orderStr = "orderStr";
	//
	// 	when(orderDetailRepository.findOrderStrByOrderDetailId(orderDetailId)).thenReturn(orderStr);
	//
	// 	String result = orderService.readOrderStr(orderDetailId);
	//
	// 	assertNotNull(result);
	// 	assertEquals(orderStr, result);
	// }
}
