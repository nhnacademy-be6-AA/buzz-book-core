package store.buzzbook.core.service.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.TagResponse;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.ProductTag;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.repository.product.ProductTagRepository;
import store.buzzbook.core.repository.product.TagRepository;

@RequiredArgsConstructor
@Service
public class TagService {

	private final TagRepository tagRepository;
	private final ProductTagRepository productTagRepository;

	@Transactional
	public TagResponse saveTag(String tagName) {
		Tag existingTag = tagRepository.findByName(tagName).orElse(null);
		if (existingTag != null) {
			return TagResponse.convertToTagResponse(existingTag);
		}
		Tag tag = new Tag(tagName);
		tagRepository.save(tag);
		return TagResponse.convertToTagResponse(tag);
	}

	@Transactional(readOnly = true)
	public List<TagResponse> getAllTags() {
		List<Tag> tagList = tagRepository.findAll();
		return tagList.stream().map(TagResponse::convertToTagResponse).toList();
	}

	@Transactional
	public Page<TagResponse> getAllTags(Pageable pageable) {
		return tagRepository.findAll(pageable).map(TagResponse::convertToTagResponse);
	}

	@Transactional(readOnly = true)
	public Page<TagResponse> getTagsByName(String name, Pageable pageable) {
		return tagRepository.findByNameContainingIgnoreCase(name, pageable).map(TagResponse::convertToTagResponse);
	}
	@Transactional(readOnly = true)
	public TagResponse getTagById(int tagId) {
		Tag tag = tagRepository.findById(tagId)
			.orElseThrow(() -> new DataNotFoundException("tag", tagId));
		return TagResponse.convertToTagResponse(tag);
	}
	@Transactional(readOnly = true)
	public TagResponse getTagByName(String tagName) {
		Tag tag = tagRepository.findByName(tagName).orElse(null);
		if (tag == null) {
			throw new DataNotFoundException("tag", tagName);
		}
		return TagResponse.convertToTagResponse(tag);
	}

	@Transactional
	public void deleteTag(int tagId) {

		productTagRepository.deleteAllByTagId(tagId);

		Tag tag = tagRepository.findById(tagId)
			.orElseThrow(() -> new DataNotFoundException("tag", tagId));
		tagRepository.delete(tag);
	}
}
