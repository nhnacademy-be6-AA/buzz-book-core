package store.buzzbook.core.repository.product.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import store.buzzbook.core.document.product.AuthorDocument;

public interface AuthorDocumentRepository extends ElasticsearchRepository<AuthorDocument, Integer> {
}
