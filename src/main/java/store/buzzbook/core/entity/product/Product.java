package store.buzzbook.core.entity.product;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private int stock;

	@Column(nullable = false, length = 255)
	private String productName;

	@Column(nullable = false)
	private int price;

	@Column(name = "forward_date")
	private ZonedDateTime forwardDate;

	@Column(nullable = false)
	private int score;

	@Column(name = "thumbnail_path")
	private String thumbnailPath;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private StockStatus stockStatus;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Builder
	public Product(int stock, String productName, int price, ZonedDateTime forwardDate,
		int score, String thumbnailPath, StockStatus stockStatus, Category category) {
		this.stock = stock;
		this.productName = productName;
		this.price = price;
		this.forwardDate = forwardDate;
		this.score = score;
		this.thumbnailPath = thumbnailPath;
		this.stockStatus = stockStatus;
		this.category = category;
	}


	public enum StockStatus {
		SALE, SOLD_OUT, OUT_OF_STOCK
	}
}

