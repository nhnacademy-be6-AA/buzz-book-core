package store.buzzbook.core.repository.product.elastic;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import store.buzzbook.core.document.product.ProductDocument;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, Integer> {
	List<ProductDocument> findByProductNameContaining(String productName);
}
