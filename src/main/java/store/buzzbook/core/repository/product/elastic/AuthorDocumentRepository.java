package store.buzzbook.core.repository.product.elastic;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import store.buzzbook.core.document.product.AuthorDocument;

public interface AuthorDocumentRepository extends ElasticsearchRepository<AuthorDocument, Integer> {
	List<AuthorDocument> findAllByName(String name);
}
