package store.buzzbook.core.service.product;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

	private final int productId = 1;
	private final int tag1Id = 11;
	private final int tag2Id = 22;

	@Mock
	private Product product;

	@Mock
	private Tag tag1, tag2;

	@Test
	void testGetTagsByProductId_Success() {

		List<ProductTag> productTags = new ArrayList<>();
		productTags.add(new ProductTag(product, tag1));
		productTags.add(new ProductTag(product, tag2));

		when(tag1.getName()).thenReturn("Tag1");
		when(tag2.getName()).thenReturn("Tag2");

		when(productTagRepository.findByProductId(productId)).thenReturn(productTags);

		// When
		List<String> tags = productTagService.getTagsByProductId(productId);

		// Then
		assertThat(tags).containsExactly("Tag1", "Tag2");
		verify(productTagRepository, times(1)).findByProductId(productId);
	}

	@Test
	void testAddTagToProduct_Success() {

		List<Integer> tagIds = List.of(1, 2);

		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag("Test Tag 1"));
		tags.add(new Tag("Test Tag 2"));
		when(tagRepository.findAllById(tagIds)).thenReturn(tags);


		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(product.getId()).thenReturn(productId);

		// When
		productTagService.addTagToProduct(productId, tagIds);

		// Then
		verify(productRepository, times(1)).findById(productId);
		verify(tagRepository, times(1)).findAllById(tagIds);
		verify(productTagRepository, times(1)).saveAll(anyList());
	}

	@Test
	void testAddTagToProduct_ProductNotFound() {

		List<Integer> tagIds = List.of(1, 2);

		when(productRepository.findById(productId)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(DataNotFoundException.class, () -> productTagService.addTagToProduct(productId, tagIds));

		verify(productRepository, times(1)).findById(productId);
		verify(tagRepository, never()).findAllById(anyList());
		verify(productTagRepository, never()).saveAll(anyList());
	}

	@Test
	void testAddTagToProduct_TagNotFound() {
		List<Integer> tagIds = List.of(1, 2);

		when(productRepository.findById(productId)).thenReturn(Optional.of(product));

		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag("Test Tag 1"));
		// Tag with ID 2 is not found

		when(tagRepository.findAllById(tagIds)).thenReturn(tags);

		// When & Then
		assertThrows(DataNotFoundException.class, () -> productTagService.addTagToProduct(productId, tagIds));

		verify(productRepository, times(1)).findById(productId);
		verify(tagRepository, times(1)).findAllById(tagIds);
		verify(productTagRepository, never()).saveAll(anyList());
	}

	@Test
	void testRemoveAllTagsFromProduct_Success() {
		List<Integer> tagIds = List.of(1, 2);

		when(productRepository.existsById(productId)).thenReturn(true);

		// When
		productTagService.removeAllTagsFromProduct(productId, tagIds);

		// Then
		verify(productRepository, times(1)).existsById(productId);
		verify(productTagRepository, times(1)).deleteTagsByProductIdAndTagIds(productId, tagIds);
	}

	@Test
	void testRemoveAllTagsFromProduct_ProductNotFound() {
		List<Integer> tagIds = List.of(1, 2);

		when(productRepository.existsById(productId)).thenReturn(false);

		// When & Then
		assertThrows(DataNotFoundException.class, () -> productTagService.removeAllTagsFromProduct(productId, tagIds));

		verify(productRepository, times(1)).existsById(productId);
		verify(productTagRepository, never()).deleteTagsByProductIdAndTagIds(anyInt(), anyList());
	}

}
