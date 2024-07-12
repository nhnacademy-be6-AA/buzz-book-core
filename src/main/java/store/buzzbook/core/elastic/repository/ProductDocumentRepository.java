package store.buzzbook.core.elastic.repository;

import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import store.buzzbook.core.elastic.document.ProductDocument;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, Integer> {

	List<ProductDocument> findByProductNameContaining(String productName);
}
