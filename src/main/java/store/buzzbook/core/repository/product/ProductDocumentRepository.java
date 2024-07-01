package store.buzzbook.core.repository.product;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

import store.buzzbook.core.document.product.ProductDocument;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, Integer> {
	List<ProductDocument> findByProductNameContaining(String productName);
	List<ProductDocument> findByCategoryName(String categoryName);
}