package store.buzzbook.core.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import store.buzzbook.core.elastic.document.PublisherDocument;

public interface PublisherDocumentRepository extends ElasticsearchRepository<PublisherDocument, Integer> {
}
