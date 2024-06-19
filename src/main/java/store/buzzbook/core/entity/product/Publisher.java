package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "publisher")
    private List<Book> books;

    public Publisher(String name) {
        this.name = name;
    }
}
