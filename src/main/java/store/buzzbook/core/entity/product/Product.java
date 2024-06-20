package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false,length = 255)
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

    @ManyToMany
    @JoinTable(name = "product_tag",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @Builder
    public Product(int stock, String productName, int price, ZonedDateTime forwardDate,
                   int score, String thumbnailPath, StockStatus stockStatus,
                   Category category) {
        this.stock = stock;
        this.productName = productName;
        this.price = price;
        this.forwardDate = forwardDate;
        this.score = score;
        this.thumbnailPath = thumbnailPath;
        this.stockStatus = stockStatus;
        this.category = category;
    }

    public enum StockStatus{
        SALE,SOLD_OUT,OUT_OF_STOCK
        }
}
