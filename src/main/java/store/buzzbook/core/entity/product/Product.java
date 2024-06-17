package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private BigDecimal price;

    private ZonedDateTime forward_date;

    @Column(nullable = false)
    private Double score;

    private String thumbnail_path;

    @OneToOne
    @JoinColumn(name = "category_id", nullable = false ,referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

}
