package store.buzzbook.core.elastic.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import store.buzzbook.core.entity.product.Publisher;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Document(indexName = "aa-bb_publisher_index")
public class PublisherDocument {

	@Id
	private Integer id;

	@Field(type = FieldType.Keyword)
	private String name;

	public PublisherDocument(Publisher publisher) {
		this.id = publisher.getId();
		this.name = publisher.getName();
	}
}
