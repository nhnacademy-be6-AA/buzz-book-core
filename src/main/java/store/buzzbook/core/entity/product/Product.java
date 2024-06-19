package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
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

    @Column(name = "forward_date")
    private ZonedDateTime forwardDate;

    @Column(nullable = false)
    private int score;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @OneToOne
    @JoinColumn(name = "category_id", nullable = false, referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

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
        this.forwardDate = localDate.atStartOfDay(ZoneId.systemDefault());
        this.score = score;
        this.thumbnailPath = thumbnail_path;
        this.category = category;
        this.book = book;
    }
}
