package store.buzzbook.core.repository.product.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import store.buzzbook.core.document.product.PublisherDocument;

public interface PublisherDocumentRepository extends ElasticsearchRepository<PublisherDocument, Integer> {
}
