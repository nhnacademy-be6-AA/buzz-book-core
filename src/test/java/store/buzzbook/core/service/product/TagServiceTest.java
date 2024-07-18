package store.buzzbook.core.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import store.buzzbook.core.dto.product.TagResponse;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.repository.product.TagRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class TagServiceTest {

	@Mock
	private TagRepository tagRepository;

	@InjectMocks
	private TagService tagService;

	@Test
	@DisplayName("태그 저장 테스트")
	void testSaveTag() {
		// given
		String tagName = "testTag";
		Tag tag = new Tag(tagName);

		when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());
		when(tagRepository.save(any(Tag.class))).thenReturn(tag);

		// when
		TagResponse tagResponse = tagService.saveTag(tagName);

		// then
		assertNotNull(tagResponse);
		assertEquals(tagName, tagResponse.getName());
		verify(tagRepository, times(1)).findByName(eq(tagName));
		verify(tagRepository, times(1)).save(any(Tag.class));
	}

	@Test
	@DisplayName("태그 조회 테스트")
	void testGetTagById() {
		// given
		int tagId = 1;
		Tag tag = new Tag("testTag");
		ReflectionTestUtils.setField(tag, "id", tagId); // id 설정

		when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

		// when
		TagResponse tagResponse = tagService.getTagById(tagId);

		// then
		assertNotNull(tagResponse);
		assertEquals(tag.getName(), tagResponse.getName());
		verify(tagRepository, times(1)).findById(tagId);
	}

	@Test
	@DisplayName("태그 삭제 테스트")
	void testDeleteTag() {
		// given
		int tagId = 1;
		Tag tag = new Tag("testTag");
		ReflectionTestUtils.setField(tag, "id", tagId); // id 설정

		when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
		doNothing().when(tagRepository).delete(tag);

		// when
		tagService.deleteTag(tagId);

		// then
		verify(tagRepository, times(1)).findById(tagId);
		verify(tagRepository, times(1)).delete(tag);
	}

	@Test
	@DisplayName("모든 태그 조회 테스트")
	void testGetAllTags() {
		// given
		Tag tag = new Tag("testTag");
		ReflectionTestUtils.setField(tag, "id", 1); // id 설정
		List<Tag> tags = Collections.singletonList(tag);

		when(tagRepository.findAll()).thenReturn(tags);

		// when
		List<TagResponse> tagResponses = tagService.getAllTags();

		// then
		assertNotNull(tagResponses);
		assertEquals(1, tagResponses.size());
		assertEquals(tag.getName(), tagResponses.get(0).getName());
		verify(tagRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("페이지별 태그 조회 테스트")
	void testGetAllTagsPageable() {
		// given
		Tag tag = new Tag("testTag");
		ReflectionTestUtils.setField(tag, "id", 1); // id 설정
		List<Tag> tags = Collections.singletonList(tag);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Tag> page = new PageImpl<>(tags, pageable, tags.size());

		when(tagRepository.findAll(pageable)).thenReturn(page);

		// when
		Page<TagResponse> tagResponses = tagService.getAllTags(pageable);

		// then
		assertNotNull(tagResponses);
		assertEquals(1, tagResponses.getTotalElements());
		assertEquals(tag.getName(), tagResponses.getContent().get(0).getName());
		verify(tagRepository, times(1)).findAll(pageable);
	}
}