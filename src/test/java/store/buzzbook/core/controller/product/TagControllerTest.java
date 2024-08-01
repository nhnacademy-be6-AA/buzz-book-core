package store.buzzbook.core.controller.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import store.buzzbook.core.dto.product.TagResponse;
import store.buzzbook.core.service.product.TagService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
class TagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TagService tagService;

	@Test
	@DisplayName("태그 추가 테스트")
	void testSaveTag() throws Exception {
		// given
		String tagName = "testTag";
		TagResponse tagResponse = new TagResponse(1, tagName);

		when(tagService.saveTag(tagName)).thenReturn(tagResponse);

		// when & then
		mockMvc.perform(post("/api/tags")
				.param("tagName", tagName)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(tagName));

		verify(tagService, times(1)).saveTag(tagName);
	}

	@Test
	@DisplayName("모든 태그 조회 테스트")
	void testGetAllTags() throws Exception {
		// given
		TagResponse tagResponse = new TagResponse(1, "testTag");
		List<TagResponse> tagResponses = Collections.singletonList(tagResponse);

		when(tagService.getAllTags()).thenReturn(tagResponses);

		// when & then
		mockMvc.perform(get("/api/tags/all")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].name").value("testTag"));

		verify(tagService, times(1)).getAllTags();
	}

	@Test
	@DisplayName("페이지별 태그 조회 테스트")
	void testGetAllTagsPageable() throws Exception {
		// given
		int pageNo = 0;
		int pageSize = 10;
		TagResponse tagResponse = new TagResponse(1, "testTag");
		PageImpl<TagResponse> tagResponses = new PageImpl<>(Collections.singletonList(tagResponse));

		when(tagService.getAllTags(any(Pageable.class))).thenReturn(tagResponses);

		// when & then
		mockMvc.perform(get("/api/tags")
				.param("pageNo", String.valueOf(pageNo))
				.param("pageSize", String.valueOf(pageSize))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].name").value("testTag"));

		verify(tagService, times(1)).getAllTags(any(Pageable.class));
	}

	@Test
	@DisplayName("ID로 태그 조회 테스트")
	void testGetTagById() throws Exception {
		// given
		int tagId = 1;
		TagResponse tagResponse = new TagResponse(tagId, "testTag");

		when(tagService.getTagById(tagId)).thenReturn(tagResponse);

		// when & then
		mockMvc.perform(get("/api/tags/{id}", tagId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("testTag"));

		verify(tagService, times(1)).getTagById(tagId);
	}

	@Test
	@DisplayName("태그 삭제 테스트")
	void testDeleteTag() throws Exception {
		// given
		int tagId = 1;

		doNothing().when(tagService).deleteTag(tagId);

		// when & then
		mockMvc.perform(delete("/api/tags/{id}", tagId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		verify(tagService, times(1)).deleteTag(tagId);
	}
}