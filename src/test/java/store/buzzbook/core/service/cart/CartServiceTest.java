package store.buzzbook.core.service.cart;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import store.buzzbook.core.common.exception.cart.CartNotExistsException;
import store.buzzbook.core.common.exception.cart.NotEnoughProductStockException;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.common.util.UuidUtil;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.cart.CreateCartDetailRequest;
import store.buzzbook.core.entity.cart.Cart;
import store.buzzbook.core.entity.cart.CartDetail;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.CartDetailRepository;
import store.buzzbook.core.repository.user.CartRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.cart.implement.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
	@Mock
	private CartRepository cartRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private CartDetailRepository cartDetailRepository;

	@InjectMocks
	private CartServiceImpl cartService;

	private Cart cart;
	private Product product;
	private CreateCartDetailRequest createCartDetailRequest;
	private CartDetail cartDetail;
	private byte[] uuid;
	private User user;
	private Grade grade;
	private String password;
	private PasswordEncoder passwordEncoder;
	private CartDetailResponse cartDetailResponse;
	private Category category;
	private List<CartDetailResponse> cartDetailList;

	@BeforeEach
	void setUp() {
		password = "asd123";
		passwordEncoder = new BCryptPasswordEncoder();
		uuid = UuidUtil.createUuidToByte();

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

		cart = Cart.builder()
			.id(1L)
			.uuid(uuid)
			.user(user)
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

		createCartDetailRequest = new CreateCartDetailRequest(1, 2);

		cartDetail = CartDetail.builder()
			.id(1L)
			.cart(cart).product(product)
			.quantity(10).build();

		cartDetailResponse = CartDetailResponse.builder()
			.id(cartDetail.getId())
			.price(product.getPrice())
			.canWrap(false)
			.categoryId(category.getId())
			.productName(product.getProductName())
			.quantity(cartDetail.getQuantity())
			.thumbnailPath(product.getThumbnailPath()).build();
		cartDetailList = new LinkedList<>();
		cartDetailList.add(cartDetailResponse);
	}

	@Test
	@DisplayName("uuid로 카트 가져오기 성공")
	void testGetCartByUuIdCartExists() {
		Mockito.when(cartRepository.findCartDetailByUuid(Mockito.any()))
			.thenReturn(Optional.of(cartDetailList));

		List<CartDetailResponse> response = cartService.getCartByUuId(UuidUtil.uuidByteToString(uuid));

		Assertions.assertNotNull(response);
		Assertions.assertFalse(response.isEmpty());
		Mockito.verify(cartRepository, Mockito.times(1)).findCartDetailByUuid(Mockito.any());
	}

	@Test
	@DisplayName("uuid로 카트 가져오기 중 존재하지 않는 카트")
	void testGetCartByUuIdCartNotExists() {
		Mockito.when(cartRepository.findCartDetailByUuid(Mockito.any())).thenReturn(Optional.empty());

		Assertions.assertThrows(CartNotExistsException.class, () -> {
			cartService.getCartByUuId(UuidUtil.uuidByteToString(uuid));
		});

		Mockito.verify(cartRepository, Mockito.times(1)).findCartDetailByUuid(Mockito.any());
	}

	@Test
	@DisplayName("userId로 카트 가져오기 성공")
	void testGetCartByUserIdCartExists() {
		Mockito.when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.of(cart));
		Mockito.when(cartRepository.findCartDetailByCartId(cart.getId()))
			.thenReturn(Optional.of(cartDetailList));

		List<CartDetailResponse> response = cartService.getCartByUserId(user.getId());

		Assertions.assertNotNull(response);
		Assertions.assertFalse(response.isEmpty());
		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUserId(user.getId());
		Mockito.verify(cartRepository, Mockito.times(1)).findCartDetailByCartId(cart.getId());
	}

	@Test
	@DisplayName("userId로 카트 가져오기 중 존재하지 않는 카트")
	void testGetCartByUserIdCartNotExists() {
		Mockito.when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.empty());
		Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cart);

		List<CartDetailResponse> response = cartService.getCartByUserId(user.getId());

		Assertions.assertNotNull(response);
		Assertions.assertTrue(response.isEmpty());
		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUserId(user.getId());
	}

	@Test
	@DisplayName("uuid로 카트상세 생성 성공")
	void testCreateCartDetailCartExists() {
		Mockito.when(cartRepository.findCartByUuid(Mockito.any())).thenReturn(Optional.of(cart));
		Mockito.when(productRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(product));
		Mockito.when(cartDetailRepository.findByProductIdAndCartId(Mockito.anyInt(), Mockito.anyLong()))
			.thenReturn(Optional.empty());

		cartService.createCartDetail(UuidUtil.uuidByteToString(uuid), createCartDetailRequest);

		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUuid(Mockito.any());
		Mockito.verify(productRepository, Mockito.times(1)).findById(Mockito.anyInt());
		Mockito.verify(cartDetailRepository, Mockito.times(1))
			.findByProductIdAndCartId(Mockito.anyInt(), Mockito.anyLong());
		Mockito.verify(cartDetailRepository, Mockito.times(1)).save(Mockito.any(CartDetail.class));
	}

	@Test
	@DisplayName("uuid로 카트상세 생성 중 카트 발견실패")
	void testCreateCartDetailCartNotExists() {
		Mockito.when(cartRepository.findCartByUuid(Mockito.any())).thenReturn(Optional.empty());

		Assertions.assertThrows(CartNotExistsException.class, () -> {
			cartService.createCartDetail(UuidUtil.uuidByteToString(uuid), createCartDetailRequest);
		});

		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUuid(Mockito.any());
	}

	@Test
	@DisplayName("uuid로 카트상세 생성 중 상품 발견실패")
	void testCreateCartDetailProductNotFound() {
		Mockito.when(cartRepository.findCartByUuid(Mockito.any())).thenReturn(Optional.of(cart));
		Mockito.when(productRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

		Assertions.assertThrows(DataNotFoundException.class, () -> {
			cartService.createCartDetail(UuidUtil.uuidByteToString(uuid), createCartDetailRequest);
		});

		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUuid(Mockito.any());
		Mockito.verify(productRepository, Mockito.times(1)).findById(Mockito.anyInt());
	}

	@Test
	@DisplayName("카트 상세 생성 중 재고 부족")
	void testCreateCartDetailProductOutOfStock() {
		product = Product.builder().score(10)
			.productName("testBook")
			.stock(0)
			.price(10000)
			.category(category)
			.stockStatus(Product.StockStatus.SALE)
			.score(10)
			.thumbnailPath("/images.png")
			.forwardDate(LocalDate.now()).build();
		Mockito.when(cartRepository.findCartByUuid(Mockito.any())).thenReturn(Optional.of(cart));
		Mockito.when(productRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(product));

		Assertions.assertThrows(NotEnoughProductStockException.class, () -> {
			cartService.createCartDetail(UuidUtil.uuidByteToString(uuid), createCartDetailRequest);
		});

		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUuid(Mockito.any());
		Mockito.verify(productRepository, Mockito.times(1)).findById(Mockito.anyInt());
	}

	@Test
	@DisplayName("카트 상세 생성 중 이미 존재하는 카트 상세")
	void testCreateCartDetailExistCartDetail() {
		Mockito.when(cartRepository.findCartByUuid(Mockito.any())).thenReturn(Optional.of(cart));
		Mockito.when(productRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(product));
		Mockito.when(cartDetailRepository.findByProductIdAndCartId(Mockito.anyInt(), Mockito.anyLong()))
			.thenReturn(Optional.of(cartDetail));

		cartService.createCartDetail(UuidUtil.uuidByteToString(uuid), createCartDetailRequest);

		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUuid(Mockito.any());
		Mockito.verify(productRepository, Mockito.times(1)).findById(Mockito.anyInt());
		Mockito.verify(cartDetailRepository, Mockito.times(1))
			.findByProductIdAndCartId(Mockito.anyInt(), Mockito.anyLong());
		Mockito.verify(cartDetailRepository, Mockito.times(1)).save(Mockito.any(CartDetail.class));
	}

	@Test
	@DisplayName("uuid로 카트상세 삭제 성공 후 카트 가져오기")
	void testDeleteCartDetailCartDetailExists() {
		Mockito.when(cartRepository.findCartDetailByUuid(Mockito.any())).thenReturn(Optional.of(cartDetailList));

		List<CartDetailResponse> response = cartService.deleteCartDetail(UuidUtil.uuidByteToString(uuid),
			cartDetail.getId());

		Mockito.verify(cartDetailRepository, Mockito.times(1)).deleteById(cartDetail.getId());
		Mockito.verify(cartRepository, Mockito.times(1)).findCartDetailByUuid(Mockito.any());
		Assertions.assertNotNull(response);
		Assertions.assertFalse(response.isEmpty());
		Assertions.assertEquals(cartDetailResponse.isCanWrap(), response.getFirst().isCanWrap());
		Assertions.assertEquals(cartDetailResponse.getPrice(), response.getFirst().getPrice());
		Assertions.assertEquals(cartDetailResponse.getProductId(), response.getFirst().getProductId());
		Assertions.assertEquals(cartDetailResponse.getProductName(), response.getFirst().getProductName());
		Assertions.assertEquals(cartDetailResponse.getThumbnailPath(), response.getFirst().getThumbnailPath());
		Assertions.assertEquals(cartDetailResponse.getCategoryId(), response.getFirst().getCategoryId());
	}

	@Test
	@DisplayName("uuid로 카트상세 삭제 후 카트 가져오기 중 비어있는 카트")
	void testDeleteCartDetailCartDetailNotExists() {
		Mockito.when(cartRepository.findCartDetailByUuid(Mockito.any())).thenReturn(Optional.empty());

		List<CartDetailResponse> response = cartService.deleteCartDetail(UuidUtil.uuidByteToString(uuid),
			cartDetail.getId());

		Mockito.verify(cartDetailRepository, Mockito.times(1)).deleteById(cartDetail.getId());
		Mockito.verify(cartRepository, Mockito.times(1)).findCartDetailByUuid(Mockito.any());
		Assertions.assertNotNull(response);
		Assertions.assertTrue(response.isEmpty());
	}

	@Test
	@DisplayName("uuid로 카트상세 전부 삭제 성공")
	void testDeleteAllCartExists() {
		Mockito.when(cartRepository.findCartByUuid(Mockito.any())).thenReturn(Optional.of(cart));

		cartService.deleteAll(UuidUtil.uuidByteToString(uuid));

		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUuid(Mockito.any());
		Mockito.verify(cartDetailRepository, Mockito.times(1)).deleteAllByCart(cart);
	}

	@Test
	@DisplayName("uuid로 카트상세 전부 삭제 실패")
	void testDeleteAllCartNotExists() {
		Mockito.when(cartRepository.findCartByUuid(Mockito.any())).thenReturn(Optional.empty());

		Assertions.assertThrows(CartNotExistsException.class, () -> {
			cartService.deleteAll(UuidUtil.uuidByteToString(uuid));
		});

		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUuid(Mockito.any());
		Mockito.verify(cartDetailRepository, Mockito.never()).deleteAllByCart(Mockito.any());
	}

	@Test
	@DisplayName("카트 상세 수량 변경 성공")
	void testUpdateCartDetailCartDetailExists() {
		int initCount = cartDetail.getQuantity();
		int expectCount = 5;
		Mockito.when(cartDetailRepository.findById(cartDetail.getId())).thenReturn(Optional.of(cartDetail));
		Mockito.when(cartDetailRepository.save(Mockito.any()))
			.thenAnswer(invocation -> {
				cartDetail = (CartDetail)invocation.getArgument(0);
				return cartDetail;
			});

		cartService.updateCartDetail(cartDetail.getId(), expectCount);

		Mockito.verify(cartDetailRepository, Mockito.times(1)).findById(cartDetail.getId());
		Mockito.verify(cartDetailRepository, Mockito.times(1)).save(cartDetail);
		Assertions.assertNotEquals(initCount, cartDetail.getQuantity());
		Assertions.assertEquals(expectCount, cartDetail.getQuantity());
	}

	@Test
	@DisplayName("카트 상세 수량 변경 중 상품 재고 부족")
	void testUpdateCartDetailProductStockNotEnough() {
		Mockito.when(cartDetailRepository.findById(cartDetail.getId())).thenReturn(Optional.of(cartDetail));

		Assertions.assertThrowsExactly(NotEnoughProductStockException.class,
			() -> cartService.updateCartDetail(cartDetail.getId(), 15));

		Mockito.verify(cartDetailRepository, Mockito.times(1)).findById(cartDetail.getId());

	}

	@Test
	@DisplayName("카트 상세 수량 변경 중 카트 발견 실패")
	void testUpdateCartDetailCartDetailNotExists() {
		Mockito.when(cartDetailRepository.findById(cartDetail.getId())).thenReturn(Optional.empty());

		Assertions.assertThrows(CartNotExistsException.class, () -> {
			cartService.updateCartDetail(cartDetail.getId(), 5);
		});

		Mockito.verify(cartDetailRepository, Mockito.times(1)).findById(cartDetail.getId());
		Mockito.verify(cartDetailRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	@DisplayName("UUID 유효성 검사 True")
	void testIsValidUUIDIsTrue() {
		Mockito.when(cartRepository.existsByUuid(Mockito.any())).thenReturn(true);

		boolean result = cartService.isValidUUID(UuidUtil.uuidByteToString(uuid));

		Assertions.assertTrue(result);
		Mockito.verify(cartRepository, Mockito.times(1)).existsByUuid(Mockito.any());
	}

	@Test
	@DisplayName("UUID 유효성 검사 False")
	void testIsValidUUIDIsFalse() {
		Mockito.when(cartRepository.existsByUuid(Mockito.any())).thenReturn(false);

		boolean result = cartService.isValidUUID(UuidUtil.uuidByteToString(uuid));

		Assertions.assertFalse(result);
		Mockito.verify(cartRepository, Mockito.times(1)).existsByUuid(Mockito.any());
	}

	@Test
	@DisplayName("회원 UUID 유효성 검사 카트 발견 실패")
	void testIsValidUUIDWithUserIdCartNotExists() {
		Mockito.when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.empty());

		Assertions.assertThrows(CartNotExistsException.class, () -> {
			cartService.isValidUUID(UuidUtil.uuidByteToString(uuid), user.getId());
		});

		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUserId(user.getId());
	}

	@Test
	@DisplayName("회원 UUID 유효성 검사 성공")
	void testIsValidUUIDWithUserIdCartExists() {
		Mockito.when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.of(cart));

		boolean result = cartService.isValidUUID(UuidUtil.uuidByteToString(uuid), user.getId());

		Assertions.assertFalse(result);
		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUserId(user.getId());
	}

	@Test
	@DisplayName("회원 카트 생성 성공")
	void testCreateCartWithUserId() {
		Mockito.when(userRepository.getReferenceById(user.getId())).thenReturn(user);
		Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

		String result = cartService.createCart(user.getId());

		Assertions.assertNotNull(result);
		Mockito.verify(userRepository, Mockito.times(1)).getReferenceById(user.getId());
		Mockito.verify(cartRepository, Mockito.times(1)).save(Mockito.any(Cart.class));
	}

	@Test
	@DisplayName("비회원 카트 생성 성공")
	void testCreateCartWithoutUserId() {
		Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

		String result = cartService.createCart();

		Assertions.assertNotNull(result);
		Mockito.verify(cartRepository, Mockito.times(1)).save(Mockito.any(Cart.class));
	}

	@Test
	@DisplayName("회원 카트 uuid 얻기 중 카트 없음")
	void testGetUuidByUserIdCartNotExists() {
		Mockito.when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.empty());
		Mockito.when(userRepository.getReferenceById(user.getId())).thenReturn(user);
		Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

		String result = cartService.getUuidByUserId(user.getId());

		Assertions.assertNotNull(result);
		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUserId(user.getId());
		Mockito.verify(cartRepository, Mockito.times(1)).save(Mockito.any(Cart.class));
	}

	@Test
	@DisplayName("회원 카트 uuid 얻기 성공")
	void testGetUuidByUserIdCartExists() {
		Mockito.when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.of(cart));

		String result = cartService.getUuidByUserId(user.getId());

		Assertions.assertNotNull(result);
		Mockito.verify(cartRepository, Mockito.times(1)).findCartByUserId(user.getId());
	}
}

