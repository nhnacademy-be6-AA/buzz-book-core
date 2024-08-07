package store.buzzbook.core.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import store.buzzbook.core.elastic.document.BookDocument;

public interface ProductDocumentRepository extends ElasticsearchRepository<BookDocument, Integer> {

}
