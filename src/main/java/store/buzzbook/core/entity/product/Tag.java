package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Product> products;

}
