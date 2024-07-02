package store.buzzbook.core.repository.product.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import store.buzzbook.core.document.product.CategoryDocument;

public interface CategoryDocumentRepository extends ElasticsearchRepository<CategoryDocument, Integer> {

}
