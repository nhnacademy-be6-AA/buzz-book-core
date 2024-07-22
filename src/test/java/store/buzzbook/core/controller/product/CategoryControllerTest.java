package store.buzzbook.core.controller.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import store.buzzbook.core.dto.product.CategoryRequest;
import store.buzzbook.core.dto.product.CategoryResponse;
import store.buzzbook.core.service.product.CategoryService;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CategoryService categoryService;

	@Mock
	private CategoryRequest testCategoryRequest;
	@Mock
	private LinkedHashMap<Integer, String> linkedHashMap;

	private CategoryResponse testCategoryResponse;
	private final int categoryId = 1;
	private final String testCategoryName = "Test Category";


	@BeforeEach
	void setUp() {
		testCategoryResponse = CategoryResponse.builder()
			.id(categoryId)
			.name(testCategoryName)
			.parentCategory(linkedHashMap)
			.subCategory(linkedHashMap)
			.build();
	}


	@Test
	@DisplayName("GET Category")
	void testGetCategory() throws Exception {

		Mockito.when(categoryService.getSubCategoriesResponse(categoryId))
			.thenReturn(testCategoryResponse);

		mockMvc.perform(get("/api/categories/{id}", categoryId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(categoryId))
			.andExpect(jsonPath("$.name").value(testCategoryName));
	}


	@Test
	@DisplayName("GET Categories")
	void testGetAllCategories() throws Exception {

		Page<CategoryResponse> categoryResponsePage = new PageImpl<>(Collections.singletonList(testCategoryResponse));

		Mockito.when(categoryService.getPageableCategoryResponses(anyInt(), anyInt())).thenReturn(categoryResponsePage);

		MvcResult result = mockMvc.perform(get("/api/categories")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn();

		String jsonPathContent = JsonPath.parse(result.getResponse().getContentAsString()).read("$.content").toString();
		String expectedContentJson = objectMapper.writeValueAsString(categoryResponsePage.getContent());
		assertEquals(expectedContentJson, jsonPathContent);
	}


	@Test
	@DisplayName("POST Category")
	void testCreateCategory() throws Exception {

		Mockito.when(categoryService.createCategory(testCategoryRequest))
			.thenReturn(testCategoryResponse);

		mockMvc.perform(post("/api/categories")
				.content(objectMapper.writeValueAsString(testCategoryRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}

	@Test
	void testUpdateCategory() throws Exception {

		Mockito.when(categoryService.updateCategory(categoryId, testCategoryRequest))
			.thenReturn(testCategoryResponse);

		mockMvc.perform(put("/api/categories/{id}", categoryId)
				.content(objectMapper.writeValueAsString(testCategoryRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	void testDeleteCategory() throws Exception {

		mockMvc.perform(delete("/api/categories/{id}", categoryId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
}
