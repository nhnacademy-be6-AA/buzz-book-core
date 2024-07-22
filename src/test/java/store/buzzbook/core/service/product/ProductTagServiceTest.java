package store.buzzbook.core.service.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.ProductTag;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.ProductTagRepository;
import store.buzzbook.core.repository.product.TagRepository;

@ExtendWith(MockitoExtension.class)
class ProductTagServiceTest {

	@Mock
	private ProductTagRepository productTagRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private TagRepository tagRepository;

	@InjectMocks
	private ProductTagService productTagService;

	@BeforeEach
	void setUp() {
		// 임시
	}

	// @Test
	// @DisplayName("상품 ID로 태그 조회 테스트")
	// void testGetTagsByProductId() {
	// 	// given
	// 	int productId = 1;
	// 	Tag tag1 = new Tag("태그1");
	// 	Tag tag2 = new Tag("태그2");
	//
	// 	ProductTag productTag1 = new ProductTag(null, tag1);
	// 	ProductTag productTag2 = new ProductTag(null, tag2);
	//
	// 	when(productTagRepository.findByProductId(productId)).thenReturn(List.of(productTag1, productTag2));
	//
	// 	// when
	// 	List<String> tags = productTagService.getTagsByProductId(productId);
	//
	// 	// then
	// 	assertNotNull(tags);
	// 	assertEquals(2, tags.size());
	// 	assertTrue(tags.contains("태그1"));
	// 	assertTrue(tags.contains("태그2"));
	// 	verify(productTagRepository, times(1)).findByProductId(productId);
	// }
	//
	// @Test
	// @DisplayName("상품에 태그 추가 테스트")
	// @Transactional
	// void testAddTagToProduct() {
	// 	// given
	// 	int productId = 1;
	// 	List<Integer> tagIds = List.of(1, 2);
	//
	// 	Product product = new Product();
	// 	Tag tag1 = mock(Tag.class);
	// 	Tag tag2 = mock(Tag.class);
	//
	// 	when(tag1.getId()).thenReturn(1);
	// 	when(tag2.getId()).thenReturn(2);
	// 	when(tag1.getName()).thenReturn("태그1");
	// 	when(tag2.getName()).thenReturn("태그2");
	//
	// 	when(productRepository.findById(productId)).thenReturn(Optional.of(product));
	// 	when(tagRepository.findAllById(tagIds)).thenReturn(List.of(tag1, tag2));
	// 	when(productTagRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
	//
	// 	// when
	// 	productTagService.addTagToProduct(productId, tagIds);
	//
	// 	// then
	// 	verify(productRepository, times(1)).findById(productId);
	// 	verify(tagRepository, times(1)).findAllById(tagIds);
	// 	verify(productTagRepository, times(1)).saveAll(anyList());
	// }
	//
	// @Test
	// @DisplayName("상품에 태그 추가 실패 테스트 - 일부 태그가 없음")
	// @Transactional
	// void testAddTagToProductTagNotFound() {
	// 	// given
	// 	int productId = 1;
	// 	List<Integer> tagIds = List.of(1, 2);
	//
	// 	Product product = new Product();
	// 	Tag tag1 = mock(Tag.class);
	//
	// 	when(tag1.getId()).thenReturn(1);
	//
	// 	when(productRepository.findById(productId)).thenReturn(Optional.of(product));
	// 	when(tagRepository.findAllById(tagIds)).thenReturn(List.of(tag1));
	//
	// 	// when & then
	// 	DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> {
	// 		productTagService.addTagToProduct(productId, tagIds);
	// 	});
	//
	// 	assertEquals("[2] (으)로 Tags 을(를) 찾을 수 없습니다.", exception.getMessage());
	// 	assertEquals("Tags", exception.getDataType());
	// 	verify(productRepository, times(1)).findById(productId);
	// 	verify(tagRepository, times(1)).findAllById(tagIds);
	// 	verify(productTagRepository, times(0)).saveAll(anyList());
	// }

	@Test
	@DisplayName("상품의 모든 태그 삭제 테스트")
	@Transactional
	void testRemoveAllTagsFromProduct() {
		// given
		int productId = 1;
		List<Integer> tagIds = List.of(1, 2);

		when(productRepository.existsById(productId)).thenReturn(true);

		// when
		productTagService.removeAllTagsFromProduct(productId, tagIds);

		// then
		verify(productRepository, times(1)).existsById(productId);
		verify(productTagRepository, times(1)).deleteTagsByProductIdAndTagIds(productId, tagIds);
	}

	@Test
	@DisplayName("상품의 모든 태그 삭제 실패 테스트 - 상품이 존재하지 않음")
	@Transactional
	void testRemoveAllTagsFromProductProductNotFound() {
		// given
		int productId = 1;
		List<Integer> tagIds = List.of(1, 2);

		when(productRepository.existsById(productId)).thenReturn(false);

		// when & then
		DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> {
			productTagService.removeAllTagsFromProduct(productId, tagIds);
		});

		assertEquals("id값 1 (으)로 product(을)를 찾을 수 없습니다.", exception.getMessage());
		assertEquals("product", exception.getDataType());
		assertEquals(1L, exception.getId());
		verify(productRepository, times(1)).existsById(productId);
		verify(productTagRepository, times(0)).deleteTagsByProductIdAndTagIds(anyInt(), anyList());
	}
}