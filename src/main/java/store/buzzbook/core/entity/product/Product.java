package store.buzzbook.core.entity.product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@Column
	private String description;

	@Column(nullable = false)
	private int price;

	@Column(name = "forward_date")
	private LocalDate forwardDate;

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

	@OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
	private List<ProductTag> productTag = new ArrayList<>();

	@Builder
	public Product(int stock, String productName, String description, int price, LocalDate forwardDate,
		int score, String thumbnailPath, StockStatus stockStatus, Category category) {
		this.stock = stock;
		this.productName = productName;
		this.description = description;
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

