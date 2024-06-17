package store.buzzbook.core.entity.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "publisher")
    private List<Book> books;

    public Publisher() {}

    @JsonCreator
    public Publisher(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}