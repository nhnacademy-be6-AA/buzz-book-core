package store.buzzbook.core.controller.cart;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.common.util.UuidUtil;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.cart.CreateCartDetailRequest;
import store.buzzbook.core.dto.user.UpdateAddressRequest;
import store.buzzbook.core.entity.cart.Cart;
import store.buzzbook.core.entity.cart.CartDetail;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.cart.CartService;

@WebMvcTest(CartController.class)
class CartControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CartService cartService;

	@Autowired
	private ObjectMapper objectMapper;
	private User user;
	private Grade grade;
	private Cart cart;
	private Product product;
	private CartDetail cartDetail;
	private Category category;
	private CreateCartDetailRequest createCartDetailRequest;
	private UpdateAddressRequest updateAddressRequest;
	private List<CartDetailResponse> cartDetailResponses;
	private PasswordEncoder passwordEncoder;
	private String password;
	private byte[] uuid;

	@BeforeEach
	void setUp() {
		uuid = UuidUtil.createUuidToByte();
		passwordEncoder = new BCryptPasswordEncoder();
		password = "password";
		grade = Grade.builder()
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();
		user = User.builder()
			.name("john doe")
			.contactNumber("01011234566")
			.email("asd123@hma.org")
			.loginId("asd123")
			.createAt(LocalDateTime.now())
			.password(passwordEncoder.encode(password))
			.lastLoginAt(LocalDateTime.now())
			.status(UserStatus.ACTIVE)
			.birthday(LocalDate.now())
			.isAdmin(false)
			.modifyAt(LocalDateTime.now())
			.id(1L)
			.build();
		category = Category.builder().name("test").build();
		product = Product.builder().score(10)
			.productName("testBook")
			.stock(10)
			.price(10000)
			.category(category)
			.stockStatus(Product.StockStatus.SALE)
			.score(10)
			.thumbnailPath("/images.png")
			.forwardDate(LocalDate.now()).build();
		cart = Cart.builder()
			.id(1L)
			.uuid(uuid)
			.user(user)
			.build();
		cartDetail = CartDetail.builder()
			.id(1L)
			.cart(cart).product(product)
			.quantity(10).build();
		createCartDetailRequest =
			new CreateCartDetailRequest(1, 1);
		cartDetailResponses = new LinkedList<>();
		cartDetailResponses.add(CartDetailResponse.builder()
			.id(cartDetail.getId())
			.price(product.getPrice())
			.canWrap(false)
			.productId(product.getId())
			.categoryId(category.getId())
			.productName(product.getProductName())
			.quantity(cartDetail.getQuantity())
			.thumbnailPath(product.getThumbnailPath()).build());
	}

	@Test
	void testGetCartByUuidFromGuest() throws Exception {
		when(cartService.getCartByUuId(UuidUtil.uuidByteToString(uuid))).thenReturn(cartDetailResponses);

		mockMvc.perform(get("/api/cart")
				.param("uuid", UuidUtil.uuidByteToString(uuid))
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, null);
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(cartDetailResponses.size()))
			.andExpect(content().json(objectMapper.writeValueAsString(cartDetailResponses)));

		verify(cartService, times(1)).getCartByUuId(anyString());
	}

	@Test
	void testGetCartByUuid_User() throws Exception {
		when(cartService.getCartByUserId(user.getId())).thenReturn(cartDetailResponses);

		mockMvc.perform(get("/api/cart")
				.param("uuid", UuidUtil.uuidByteToString(uuid))
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, user.getId());
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(cartDetailResponses.size()))
			.andExpect(content().json(objectMapper.writeValueAsString(cartDetailResponses)));

		verify(cartService, times(1)).getCartByUserId(anyLong());
	}

	@Test
	void testCreateCartDetail() throws Exception {
		doNothing().when(cartService).createCartDetail(UuidUtil.uuidByteToString(uuid), createCartDetailRequest);

		mockMvc.perform(post("/api/cart/detail")
				.param("uuid", UuidUtil.uuidByteToString(uuid))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createCartDetailRequest)))
			.andExpect(status().isOk());

		verify(cartService, times(1)).createCartDetail(anyString(), any(CreateCartDetailRequest.class));
	}

	@Test
	void testDeleteCartDetail() throws Exception {
		when(cartService.deleteCartDetail(UuidUtil.uuidByteToString(uuid), cartDetail.getId())).thenReturn(
			cartDetailResponses);

		mockMvc.perform(delete("/api/cart/detail/" + cartDetail.getId())
				.param("uuid", UuidUtil.uuidByteToString(uuid)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(cartDetailResponses.size()))
			.andExpect(content().json(objectMapper.writeValueAsString(cartDetailResponses)));

		verify(cartService, times(1)).deleteCartDetail(anyString(), anyLong());
	}

	@Test
	void testDeleteAllCartDetail() throws Exception {
		doNothing().when(cartService).deleteAll(UuidUtil.uuidByteToString(uuid));

		mockMvc.perform(delete("/api/cart")
				.param("uuid", UuidUtil.uuidByteToString(uuid)))
			.andExpect(status().isOk());

		verify(cartService, times(1)).deleteAll(anyString());
	}

	@Test
	void testUpdateCartDetail() throws Exception {
		int quantity = 2;
		doNothing().when(cartService).updateCartDetail(cartDetail.getId(), quantity);

		mockMvc.perform(put("/api/cart/detail/" + cartDetail.getId())
				.param("uuid", UuidUtil.uuidByteToString(uuid))
				.param("quantity", String.valueOf(quantity)))
			.andExpect(status().isOk());

		verify(cartService, times(1)).updateCartDetail(anyLong(), anyInt());
	}

	@Test
	void testGetUuidByUserId() throws Exception {
		when(cartService.getUuidByUserId(user.getId())).thenReturn(UuidUtil.uuidByteToString(uuid));

		mockMvc.perform(get("/api/cart/uuid")
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, user.getId());
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(content().string(UuidUtil.uuidByteToString(uuid)));

		verify(cartService, times(1)).getUuidByUserId(anyLong());
	}

	@Test
	void testCreateCart() throws Exception {
		when(cartService.createCart()).thenReturn(UuidUtil.uuidByteToString(uuid));

		mockMvc.perform(get("/api/cart/guest"))
			.andExpect(status().isOk())
			.andExpect(content().string(UuidUtil.uuidByteToString(uuid)));

		verify(cartService, times(1)).createCart();
	}
}
