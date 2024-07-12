package store.buzzbook.core.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import store.buzzbook.core.elastic.document.AuthorDocument;

public interface AuthorDocumentRepository extends ElasticsearchRepository<AuthorDocument, Integer> {
}
