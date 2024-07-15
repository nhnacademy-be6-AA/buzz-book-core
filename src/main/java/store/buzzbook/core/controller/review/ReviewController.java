package store.buzzbook.core.controller.review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.review.IllegalRequestException;
import store.buzzbook.core.dto.review.ReviewCreateRequest;
import store.buzzbook.core.dto.review.ReviewResponse;
import store.buzzbook.core.dto.review.ReviewUpdateRequest;
import store.buzzbook.core.service.review.ReviewService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Tag(name = "리뷰 관리", description = "리뷰 CRU_")
public class ReviewController {

	private final ReviewService reviewService;
	private final ObjectMapper objectMapper;

	@PostMapping
	@Operation(summary = "리뷰 추가", description = "새로운 리뷰 등록")
	@ApiResponse(responseCode = "200", description = "리뷰 등록 성공시 등록된 리뷰의 ReviewResponse 반환")

	public ResponseEntity<ReviewResponse> saveReview(
		@RequestPart("reviewCreateRequest") ReviewCreateRequest reviewCreateRequest,
		@RequestPart(value = "file", required = false) MultipartFile file) {

		return ResponseEntity.ok(reviewService.saveReview(reviewCreateRequest, file));
	}



	@GetMapping("/{reviewId}")
	@Operation(summary = "리뷰 1건 조회")
	@ApiResponse(responseCode = "200", description = "조회 성공시 ReviewResponse 반환")

	public ResponseEntity<ReviewResponse> getReview(@PathVariable int reviewId) {
		return ResponseEntity.ok(reviewService.getReview(reviewId));
	}



	@GetMapping
	@Operation(summary = "리뷰 조회", description = "주어진 parameter로 리뷰 조회")
	@ApiResponse(responseCode = "200", description = "조회 성공시 Page<ReviewResponse> 반환")

	public ResponseEntity<Page<ReviewResponse>> getAllReviews(
		@RequestParam(required = false) @Parameter(description = "상품 id(long)에 해당하는 모든 리뷰 조회") Integer productId,
		@RequestParam(required = false) @Parameter(description = "유저 id(long)로 해당 유저가 작성한 모든 리뷰 조회") Long userId,
		@RequestParam(required = false, defaultValue = "0") @Parameter(description = "페이지 번호") Integer pageNo,
		@RequestParam(required = false, defaultValue = "5") @Parameter(description = "한 페이지에 보여질 아이템 수") Integer pageSize) {

		if (productId != null) {
			return ResponseEntity.ok(reviewService.findAllReviewByProductId(productId, pageNo, pageSize));
		}
		if (userId != null) {
			return ResponseEntity.ok(reviewService.findAllReviewByUserId(userId, pageNo, pageSize));
		}
		return ResponseEntity.badRequest().build();
	}



	@PutMapping("/{reviewId}")
	@Operation(summary = "리뷰 수정", description = "리뷰 수정")
	@ApiResponse(responseCode = "200", description = "리뷰 수정 성공시 수정된 리뷰의 ReviewResponse 반환")

	public ResponseEntity<ReviewResponse> updateReview(@Validated @RequestBody ReviewUpdateRequest reviewReq,
		@PathVariable Long reviewId) {

		if (reviewReq.getId() != reviewId) {
			throw new IllegalRequestException("id가 일치하는 리뷰를 수정요청하세요.");
		}
		return ResponseEntity.ok(reviewService.updateReview(reviewReq));
	}

}
