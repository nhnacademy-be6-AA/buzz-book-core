// package store.buzzbook.core.elasticsearch.elastic.document;
//
// import org.springframework.data.annotation.Id;
// import org.springframework.data.elasticsearch.annotations.Document;
// import org.springframework.data.elasticsearch.annotations.Field;
// import org.springframework.data.elasticsearch.annotations.FieldType;
//
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
// import lombok.Data;
// import lombok.Getter;
// import store.buzzbook.core.entity.product.Tag;
//
// @Data
// @JsonIgnoreProperties(ignoreUnknown = true)
// @Getter
// @Document(indexName = "aa-bb_tag_index")
// public class TagDocument {
//
// 	@Id
// 	private Integer id;
//
// 	@Field(type = FieldType.Keyword)
// 	private String name;
//
// 	public TagDocument(Tag tag) {
// 		id = tag.getId();
// 		name = tag.getName();
// 	}
// }
