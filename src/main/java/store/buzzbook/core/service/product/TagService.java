package store.buzzbook.core.service.product;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.TagResponse;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.repository.product.TagRepository;

@RequiredArgsConstructor
@Service
public class TagService {

	private final TagRepository tagRepository;

	public TagResponse saveTag(String tagName) {
		Tag tag = new Tag(tagName);
			tagRepository.save(tag);
		return TagResponse.convertToTagResponse(tag);
	}

	public List<TagResponse> getAllTags() {
		List<Tag> tagList = tagRepository.findAll();
		return tagList.stream().map(TagResponse::convertToTagResponse).toList();
	}

	public TagResponse getTagById(int tagId) {
		Tag tag = tagRepository.findById(tagId)
			.orElseThrow(() -> new DataNotFoundException("tag", tagId));
		return TagResponse.convertToTagResponse(tag);
	}

	public TagResponse getTagByName(String tagName) {
		Tag tag = tagRepository.findByName(tagName).orElse(null);
		if (tag == null) {
			throw new DataNotFoundException("tag", tagName);
		}
		return TagResponse.convertToTagResponse(tag);
	}

	public void deleteTag(int tagId) {
		Tag tag = tagRepository.findById(tagId)
			.orElseThrow(() -> new DataNotFoundException("tag", tagId));
		tagRepository.delete(tag);
	}
}
