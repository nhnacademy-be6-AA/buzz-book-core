package store.buzzbook.core.service.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.ProductTag;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.ProductTagRepository;
import store.buzzbook.core.repository.product.TagRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductTagService {

	private final ProductTagRepository productTagRepository;
	private final ProductRepository productRepository;
	private final TagRepository tagRepository;

	public List<String> getTagsByProductId(int productId) {
		return productTagRepository.findByProductId(productId).stream()
			.map(productTag -> productTag.getTag().getName()).toList();
	}

	@Transactional
	public void addTagToProduct(int productId, List<Integer> tagIds) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new DataNotFoundException("product", productId));

		List<Tag> tags = tagRepository.findAllById(tagIds);

		if (tags.size() != tagIds.size()) {
			List<Integer> foundTagIds = tags.stream().map(Tag::getId).toList();
			List<Integer> notFoundTagIds = tagIds.stream().filter(id -> !foundTagIds.contains(id)).toList();
			throw new DataNotFoundException("Tags", notFoundTagIds.toString());
		}

		//새로운 ProductTag 엔티티를 생성 & 저장
		List<ProductTag> productTags = tags.stream()
			.map(tag -> new ProductTag(product, tag))
			.toList();

		productTagRepository.saveAll(productTags);
	}

	//특정 상품에 대한 연결 삭제
	@Transactional
	public void removeTagFromProduct(int productId, int tagId) {
		if (!productRepository.existsById(productId)) {
			throw new DataNotFoundException("product", productId);
		}
		productTagRepository.deleteByProductIdAndTagId(productId, tagId);
	}

	@Transactional
	public void removeAllTagsFromProduct(int productId, List<Integer> tagIds) {
		if (!productRepository.existsById(productId)) {
			throw new DataNotFoundException("product", productId);
		}
		productTagRepository.deleteAllByProductIdAndTagIdIn(productId, tagIds);

	}
}

//보는 사람 지우지 마세요 ㅎㅎㅎ	//넹 ㅎㅎㅎ
//태그 관리 페이지를 통해서 태그 추가
//관리 페이지에서 상품 편집을 통해 태그 추가하면 productTag에 추가됌
