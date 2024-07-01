package store.buzzbook.core.document.product;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.ProductTag;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "aa-bb_product_index")
@AllArgsConstructor
public class ProductDocument {

	@Id
	private int id;

	@Field(type = FieldType.Integer)
	private int stock;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String productName;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String description;

	@Field(type = FieldType.Integer, index = false)
	private int price;

	@Field(type = FieldType.Date)
	private LocalDate forwardDate;

	@Field(type = FieldType.Integer, index = false)
	private int score;

	@Field(type = FieldType.Keyword, index = false,  docValues = false)
	private String thumbnailPath;

	@Field(type = FieldType.Keyword)
	private String stockStatus;

	@Field(type = FieldType.Integer)
	private Integer category_id;

	@Field(type = FieldType.Object)
	private List<TagDocument> tags;


	public static ProductDocument convertToProductDocument(Product product) {
		return new ProductDocument(product.getId(), product.getStock(),
			product.getProductName(), product.getDescription(), product.getPrice(), product.getForwardDate(),
			product.getScore(), product.getThumbnailPath(), product.getStockStatus().toString(), product.getCategory().getId(),
			product.getProductTags().stream().map(ProductTag::getTag).map(TagDocument::new).toList());
	}
}
