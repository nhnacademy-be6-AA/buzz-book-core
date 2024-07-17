package store.buzzbook.core.repository.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import store.buzzbook.core.common.config.QuerydslConfig;
import store.buzzbook.core.common.util.UuidUtil;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.entity.cart.Cart;
import store.buzzbook.core.entity.cart.CartDetail;
import store.buzzbook.core.entity.cart.Wishlist;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.Publisher;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;

@Import(QuerydslConfig.class)
@DataJpaTest
@Disabled
@ActiveProfiles("test")
class CartWishlistRepositoryTest {
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private GradeRepository gradeRepository;
	@Autowired
	private UserRepository userRepository;

	private Book book;
	private Cart cart;
	private Grade grade;
	private User user;
	@Autowired
	private CartDetailRepository cartDetailRepository;
	@Autowired
	private WishlistRepository wishlistRepository;

	private Product product;
	private Publisher publisher;
	private byte[] uuidByte;

	@BeforeEach
	void setUp() {
		uuidByte = UuidUtil.createUuidToByte();

		grade = Grade.builder()
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		gradeRepository.save(grade);

		user = User.builder()
			.loginId("dfsajkh23489y")
			.name("john doe")
			.email("email123@nhn.com")
			.contactNumber("010-0000-1111")
			.birthday(LocalDate.now())
			.modifyAt(LocalDateTime.now())
			.createAt(LocalDateTime.now())
			.password("encrytedsolongpassword123345")
			.lastLoginAt(LocalDateTime.now())
			.status(UserStatus.ACTIVE).build();
		userRepository.save(user);

		cart = Cart.builder()
			.user(user)
			.uuid(uuidByte)
			.build();

		Category category = Category.builder().name("test").build();

		entityManager.persist(category);

		publisher = new Publisher("pub name");

		entityManager.persist(publisher);

		book = Book.builder()
			.title("OO")
			.isbn("1234567890123")
			.publishDate("2012-01-01")
			.publisher(publisher)
			.description("desc").build();

		entityManager.persist(book);

		product = Product.builder().score(10)
			.productName("testBook")
			.stock(100)
			.price(10000)
			.category(category)
			.stockStatus(Product.StockStatus.SALE)
			.score(10)
			.thumbnailPath("/images.png")
			.forwardDate(LocalDate.now()).build();
	}

	@Test
	@Disabled("h2 type 문제")
	@DisplayName("장바구니 생성 테스트")
	void testCreateCart() {
		cartRepository.save(cart);
		Cart resultCart = cartRepository.findById(cart.getId()).orElse(null);

		Assertions.assertNotNull(resultCart);
		Assertions.assertEquals(cart.getId(), resultCart.getId());
		Assertions.assertEquals(cart.getUser().getId(), resultCart.getUser().getId());

	}

	//book fk 필요
	@Test
	@DisplayName("장바구니 상품 추가 테스트")
	@Disabled("h2 type 문제")
	void testCreateCartDetail() {
		cartRepository.save(cart);

		entityManager.persist(product);

		CartDetail cartDetail = CartDetail.builder()
			.cart(cart).product(product)
			.quantity(10).build();

		cartDetailRepository.save(cartDetail);

		CartDetail result = cartDetailRepository.findById(cartDetail.getId()).orElse(null);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(cartDetail.getId(), result.getId());
		Assertions.assertEquals(cartDetail.getProduct().getId(), product.getId());
		Assertions.assertEquals(cartDetail.getQuantity(), result.getQuantity());
	}

	@Test
	@Disabled("h2 type 문제")
	@DisplayName("장바구니 상품 삭제 테스트")
	void testCreateAndDeleteDetail() {
		cartRepository.save(cart);

		entityManager.persist(product);

		CartDetail cartDetail = CartDetail.builder()
			.cart(cart).product(product)
			.quantity(10).build();

		cartDetailRepository.save(cartDetail);

		CartDetail result = cartDetailRepository.findById(cartDetail.getId()).orElse(null);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(cartDetail.getId(), result.getId());
		Assertions.assertEquals(cartDetail.getProduct().getId(), product.getId());
		Assertions.assertEquals(cartDetail.getQuantity(), result.getQuantity());

		cartDetailRepository.deleteById(cartDetail.getId());

		result = cartDetailRepository.findById(cartDetail.getId()).orElse(null);
		Assertions.assertNull(result);
	}

	@Test
	@DisplayName("위시리스트 추가 테스트")
	void testCreateWishList() {
		Wishlist wishlist = Wishlist.builder()
			.product(product)
			.user(user).build();

		entityManager.persist(product);

		wishlistRepository.save(wishlist);
		Wishlist result = wishlistRepository.findById(wishlist.getId()).orElse(null);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(wishlist.getProduct().getId(), product.getId());

	}

	@Test
	@Disabled("h2 type 문제")
	@DisplayName("카트아이디로 카트 내용 찾기")
	void testFindCartDetailByCartId() {
		entityManager.persist(product);
		cartRepository.save(cart);
		CartDetail cartDetail = CartDetail.builder()
			.cart(cart).product(product)
			.quantity(10).build();
		cartDetailRepository.save(cartDetail);

		List<CartDetailResponse> cartDetailResponseList = cartRepository.findCartDetailByCartId(cart.getId())
			.orElse(null);

		Assertions.assertNotNull(cartDetailResponseList);

		CartDetailResponse cartDetailResponse = cartDetailResponseList.getFirst();

		Assertions.assertEquals(1, cartDetailResponseList.size());
		Assertions.assertEquals(cartDetail.getId(), cartDetailResponse.getId());
		Assertions.assertEquals(cartDetail.getProduct().getId(), cartDetailResponse.getProductId());
		Assertions.assertEquals(cartDetail.getQuantity(), cartDetailResponse.getQuantity());
		Assertions.assertEquals(cartDetail.getProduct().getThumbnailPath(), cartDetailResponse.getThumbnailPath());
		Assertions.assertEquals(product.getPrice(), cartDetailResponse.getPrice());
		Assertions.assertFalse(cartDetailResponse.isCanWrap());
		Assertions.assertEquals(cartDetail.getProduct().getProductName(), cartDetailResponse.getProductName());
	}
}
