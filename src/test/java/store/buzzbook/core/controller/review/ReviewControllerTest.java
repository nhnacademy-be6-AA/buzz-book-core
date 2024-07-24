package store.buzzbook.core.controller.review;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.dto.review.ReviewRequest;
import store.buzzbook.core.dto.review.ReviewResponse;
import store.buzzbook.core.service.review.ReviewService;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ReviewService reviewService;

	@Mock
	ReviewResponse mockReviewResponse;
	@Qualifier("objectMapper")

	ReviewRequest testReviewRequest;

	String content = "Great book!";
	int reviewScore = 5;
	long orderDetailId = 1L;

	@BeforeEach
	void setUp() {
		testReviewRequest = new ReviewRequest(content, reviewScore, orderDetailId);
	}

	@Test
	@DisplayName("POST review")
	void postReview() throws Exception {
		List<MultipartFile> mockFiles = new ArrayList<>();

		when(reviewService.saveReview(any(ReviewRequest.class), anyList())).thenReturn(mockReviewResponse);

		mockMvc.perform(post("/api/reviews")
				.param("content", content)
				.param("reviewScore", String.valueOf(reviewScore))
				.param("orderDetailId", String.valueOf(orderDetailId))
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.content(objectMapper.writeValueAsString(mockFiles)))
			.andExpect(status().isOk());

	}


	@Test
	@DisplayName("GET review")
	void testGetReview() throws Exception {

		when(reviewService.getReview(anyInt())).thenReturn(mockReviewResponse);

		mockMvc.perform(get("/api/reviews/1"))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET reviews")
	void testGetReviews() throws Exception {

		Integer productId = 2;
		Long userId = 3L;


		mockMvc.perform(get("/api/reviews")
			.param("orderDetailId", String.valueOf(orderDetailId)))
			.andExpect(status().isOk());

		mockMvc.perform(get("/api/reviews")
			.param("productId", String.valueOf(productId)))
			.andExpect(status().isOk());

		mockMvc.perform(get("/api/reviews")
			.param("userId", String.valueOf(userId)))
			.andExpect(status().isOk());

		mockMvc.perform(get("/api/reviews"))
			.andExpect(status().isBadRequest());

		mockMvc.perform(get("/api/reviews")
			.param("wrongParam", "something"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT review")
	void testUpdateReview() throws Exception {

		int reviewId = 5;

		when(reviewService.updateReview(reviewId, testReviewRequest)).thenReturn(mockReviewResponse);

		mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
				.content(objectMapper.writeValueAsString(testReviewRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

}
