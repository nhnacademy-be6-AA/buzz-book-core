package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int price;

    private ZonedDateTime forward_date;

    @Column(nullable = false)
    private int score;

    private String thumbnail_path;

    @OneToOne
    @JoinColumn(name = "category_id", nullable = false, referencedColumnName = "id")
    private Category category;

    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Builder
    public Product(int stock, int price, String forward_date,
            int score, String thumbnail_path, Category category,Book book)
    {
        this.stock = stock;
        this.price = price;
        LocalDate localDate = LocalDate.parse(forward_date);
        this.forward_date = localDate.atStartOfDay(ZoneId.systemDefault());
        this.score = score;
        this.thumbnail_path = thumbnail_path;
        this.category = category;
        this.book = book;
    }
}
