package store.buzzbook.core.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// public class ProductTagServiceTest {
//
// 	@Mock
// 	private ProductTagRepository productTagRepository;
//
// 	@Mock
// 	private ProductRepository productRepository;
//
// 	@Mock
// 	private TagRepository tagRepository;
//
// 	@InjectMocks
// 	private ProductTagService productTagService;
//
// 	@BeforeEach
// 	void setUp() {
// 		MockitoAnnotations.openMocks(this);
// 	}
//
// 	@Test
// 	@DisplayName("상품 ID로 태그 조회 테스트")
// 	void testGetTagsByProductId() {
// 		// given
// 		int productId = 1;
// 		Product product = new Product();
// 		Tag tag = new Tag("testTag");
// 		ProductTag productTag = new ProductTag(product, tag);
//
// 		when(productTagRepository.findByProductId(productId)).thenReturn(List.of(productTag));
//
// 		// when
// 		List<String> tags = productTagService.getTagsByProductId(productId);
//
// 		// then
// 		assertNotNull(tags);
// 		assertEquals(1, tags.size());
// 		assertEquals("testTag", tags.get(0));
// 		verify(productTagRepository, times(1)).findByProductId(productId);
// 	}
//
// 	@Test
// 	@DisplayName("상품에 태그 추가 테스트")
// 	void testAddTagToProduct() {
// 		// given
// 		int productId = 1;
// 		List<Integer> tagIds = List.of(1, 2);
// 		Product product = new Product();
// 		Tag tag1 = new Tag("tag1");
// 		Tag tag2 = new Tag("tag2");
//
// 		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
// 		when(tagRepository.findAllById(tagIds)).thenReturn(List.of(tag1, tag2));
//
// 		// when
// 		productTagService.addTagToProduct(productId, tagIds);
//
// 		// then
// 		verify(productRepository, times(1)).findById(productId);
// 		verify(tagRepository, times(1)).findAllById(tagIds);
// 		verify(productTagRepository, times(1)).saveAll(anyList());
// 	}
//
// 	@Test
// 	@DisplayName("상품에 태그 추가 실패 테스트 - 태그 일부 없음")
// 	void testAddTagToProductTagNotFound() {
// 		// given
// 		int productId = 1;
// 		List<Integer> tagIds = List.of(1, 2);
// 		Product product = new Product();
// 		Tag tag1 = new Tag("tag1");
//
// 		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
// 		when(tagRepository.findAllById(tagIds)).thenReturn(List.of(tag1));
//
// 		// when & then
// 		Exception exception = assertThrows(DataNotFoundException.class, () -> {
// 			productTagService.addTagToProduct(productId, tagIds);
// 		});
//
// 		String expectedMessage = "Tags";
// 		String actualMessage = exception.getMessage();
//
// 		assertTrue(actualMessage.contains(expectedMessage));
// 		verify(productRepository, times(1)).findById(productId);
// 		verify(tagRepository, times(1)).findAllById(tagIds);
// 	}
//
// 	@Test
// 	@DisplayName("상품에서 태그 삭제 테스트")
// 	void testRemoveAllTagsFromProduct() {
// 		// given
// 		int productId = 1;
// 		List<Integer> tagIds = List.of(1, 2);
//
// 		when(productRepository.existsById(productId)).thenReturn(true);
//
// 		// when
// 		productTagService.removeAllTagsFromProduct(productId, tagIds);
//
// 		// then
// 		verify(productRepository, times(1)).existsById(productId);
// 		verify(productTagRepository, times(1)).deleteTagsByProductIdAndTagIds(productId, tagIds);
// 	}
//
// 	@Test
// 	@DisplayName("상품에서 태그 삭제 실패 테스트 - 상품 없음")
// 	void testRemoveAllTagsFromProductProductNotFound() {
// 		// given
// 		int productId = 1;
// 		List<Integer> tagIds = List.of(1, 2);
//
// 		when(productRepository.existsById(productId)).thenReturn(false);
//
// 		// when & then
// 		Exception exception = assertThrows(DataNotFoundException.class, () -> {
// 			productTagService.removeAllTagsFromProduct(productId, tagIds);
// 		});
//
// 		String expectedMessage = "product";
// 		String actualMessage = exception.getMessage();
//
// 		assertTrue(actualMessage.contains(expectedMessage));
// 		verify(productRepository, times(1)).existsById(productId);
// 		verify(productTagRepository, times(0)).deleteTagsByProductIdAndTagIds(productId, tagIds);
// 	}
// }