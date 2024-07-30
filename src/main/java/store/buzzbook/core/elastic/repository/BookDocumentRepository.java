package store.buzzbook.core.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import store.buzzbook.core.elastic.document.BookDocument;

@Repository
public interface BookDocumentRepository extends ElasticsearchRepository<BookDocument, Long> {

}