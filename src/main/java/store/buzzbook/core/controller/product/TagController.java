package store.buzzbook.core.controller.product;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.TagResponse;
import store.buzzbook.core.service.product.TagService;

@RestController
@RequiredArgsConstructor
@Tag(name = "태그 관리", description = "태그 C_UD")
public class TagController {

	private final TagService tagService;

	@PostMapping
	@Operation(summary = "태그 추가", description = "새로운 태그 등록")
	@ApiResponse(responseCode = "200", description = "상품 추가 성공시 추가된 태그의 TagResponse 반환")

	public ResponseEntity<TagResponse> saveTag(@RequestParam String tagName) {
		return ResponseEntity.ok(tagService.saveTag(tagName));
	}

	@GetMapping
	@Operation(summary = "태그 조회", description = "주어진 (String) 조회<br>태그를 id 순서로 조회<br>주어진 id(int)에 해당하는 태그 조회")
	@ApiResponse(responseCode = "200", description = "조회 성공시 List<TagResponse> 반환<br>태그 이름(String) 값 존재하면 이름으로 조회한 태그 반환")

	public ResponseEntity<List<TagResponse>> getAllTags(
		@RequestParam(required = false) @Parameter(description = "검색할 태그이름(정확히 일치 해야함)") String tagName) {
		if (tagName == null) {
			return ResponseEntity.ok(tagService.getAllTags());
		}
		return ResponseEntity.ok(Collections.singletonList(tagService.getTagByName(tagName)));
	}

	@GetMapping({"/{id}"})
	@Operation(summary = "태그 조회", description = "태그 조회<br>태그를 id 순서로 조회<br>주어진 id(int)에 해당하는 태그 조회")
	@ApiResponse(responseCode = "200", description = "조회 성공시 TagResponse 반환")

	public ResponseEntity<TagResponse> getTagById(@PathVariable int id) throws DataNotFoundException {
		return ResponseEntity.ok(tagService.getTagById(id));
	}

	@DeleteMapping
	@Operation(summary = "태그 삭제", description = "주어진 id(int)에 해당하는 태그 삭제.")
	@ApiResponse(responseCode = "204", description = "삭제 성공시")
	public ResponseEntity<Void> deleteTag(@RequestParam int tagId) {
		tagService.deleteTag(tagId);
		return ResponseEntity.noContent().build();
	}
}
