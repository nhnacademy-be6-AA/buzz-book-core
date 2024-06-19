package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name; //카테고리명

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Category(String name, Category parentCategory) {}


}