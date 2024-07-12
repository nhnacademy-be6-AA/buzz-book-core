package store.buzzbook.core.elastic.repository;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, Integer> {
	List<ProductDocument> findByProductNameContaining(String productName);
}
