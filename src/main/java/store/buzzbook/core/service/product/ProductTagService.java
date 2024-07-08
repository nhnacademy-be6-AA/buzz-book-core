package store.buzzbook.core.service.product;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.ProductTag;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.ProductTagRepository;
import store.buzzbook.core.repository.product.TagRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
	public void removeAllTagsFromProduct(int productId, List<Integer> tagIds) {
		if (!productRepository.existsById(productId)) {
			throw new DataNotFoundException("product", productId);
		}
		productTagRepository.deleteTagsByProductIdAndTagIds(productId, tagIds);

	}
}

//보는 사람 지우지 마세요 ㅎㅎㅎ
//태그 관리 페이지를 통해서 태그 추가
//관리 페이지에서 상품 편집을 통해 태그 추가하면 productTag에 추가됌