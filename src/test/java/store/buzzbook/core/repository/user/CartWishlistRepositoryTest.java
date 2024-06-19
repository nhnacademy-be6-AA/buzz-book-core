package store.buzzbook.core.repository.user;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import store.buzzbook.core.entity.cart.Cart;
import store.buzzbook.core.entity.cart.CartDetail;
import store.buzzbook.core.entity.cart.Wishlist;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@DataJpaTest
class CartWishlistRepositoryTest {
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private GradeRepository gradeRepository;
	@Autowired
	private UserRepository userRepository;

	private Cart cart;
	private Grade grade;
	private User user;
	@Autowired
	private CartDetailRepository cartDetailRepository;
	@Autowired
	private WishlistRepository wishlistRepository;

	private Product product;

	@BeforeEach
	void setUp() {

		grade = Grade.builder()
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		gradeRepository.save(grade);

		user = User.builder()
			.loginId("asd123")
			.name("john doe")
			.grade(grade)
			.email("email123@nhn.com")
			.contactNumber("010-0000-1111")
			.birthday(ZonedDateTime.now())
			.modifyDate(ZonedDateTime.now())
			.createDate(ZonedDateTime.now())
			.password("encrytedsolongpassword123345")
			.lastLoginDate(ZonedDateTime.now())
			.status(UserStatus.ACTIVE).build();
		userRepository.save(user);

		cart = Cart.builder()
			.user(user)
			.cartUpdatedDate(ZonedDateTime.now()).build();

		Category category = Category.builder().name("test").build();

		entityManager.persist(category);

		product = Product.builder().score(10)
			.stock(100)
			.price(BigDecimal.valueOf(10000L))
			.category(category).build();

	}


	@Test
	@DisplayName("장바구니 생성 테스트")
	void testCreateCart() {
		cartRepository.save(cart);
		Cart resultCart = cartRepository.findById(cart.getId()).orElse(null);

		Assertions.assertNotNull(resultCart);
		Assertions.assertEquals(cart.getId(), resultCart.getId());
		Assertions.assertEquals(cart.getUser().getId(), resultCart.getUser().getId());



	}

	@Test
	@DisplayName("장바구니 상품 추가 테스트")
	void testCreateCartDetail(){
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
	@DisplayName("장바구니 상품 삭제 테스트")
	void testCreateAndDeleteDetail(){
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
	void testCreateWishList(){
		Wishlist wishlist = Wishlist.builder()
				.product(product)
				.user(user).build();

		entityManager.persist(product);

		wishlistRepository.save(wishlist);
		Wishlist result = wishlistRepository.findById(wishlist.getWishlistId()).orElse(null);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(wishlist.getProduct().getId(), product.getId());

	}
}
