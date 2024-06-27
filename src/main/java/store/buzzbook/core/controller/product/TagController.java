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
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.TagResponse;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.service.product.TagService;

@RestController
@RequiredArgsConstructor
public class TagController {

	private final TagService tagService;

	@PostMapping
	@Operation(summary = "tag 등록", description = "string 태그 등록")
	public ResponseEntity<Tag> saveTag(@RequestParam String tagName) {
		Tag tag = tagService.saveTag(tagName);
		return ResponseEntity.ok(tag);
	}

	@GetMapping
	public ResponseEntity<List<TagResponse>> getAllTags(@RequestParam(required = false) String tagName) {
		if (tagName == null) {
			return ResponseEntity.ok(tagService.getAllTags());
		}
		return ResponseEntity.ok(Collections.singletonList(tagService.getTagByName(tagName)));
	}

	@GetMapping({"/{id}"})
	public ResponseEntity<TagResponse> getTagById(@PathVariable int id) throws DataNotFoundException {
		return ResponseEntity.ok(tagService.getTagById(id));
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteTag(@RequestParam int tagId) {
		tagService.deleteTag(tagId);
		return ResponseEntity.noContent().build();
	}
}
