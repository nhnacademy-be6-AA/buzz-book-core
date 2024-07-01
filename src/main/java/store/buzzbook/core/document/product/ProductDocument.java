package store.buzzbook.core.document.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import store.buzzbook.core.entity.product.Product;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "product_index")
public class ProductDocument {
	@Id
	private int id;

	@Field(type = FieldType.Integer)
	private int stock;

	@Field(type = FieldType.Text)
	private String productName;

	@Field(type = FieldType.Text)
	private String description;

	@Field(type = FieldType.Integer)
	private int price;

	@Field(type = FieldType.Date)
	private LocalDate forwardDate;

	@Field(type = FieldType.Integer)
	private int score;

	@Field(type = FieldType.Text)
	private String thumbnailPath;

	@Field(type = FieldType.Keyword)
	private Product.StockStatus stockStatus;

	// 새로운 필드 추가
	@Field(type = FieldType.Text)
	private String categoryName;

}