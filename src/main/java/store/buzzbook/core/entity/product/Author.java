package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Table(name = "author")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(length = 10,nullable = true)
    private String role;

    @OneToMany(mappedBy = "author")
    private List<BookAuthor> bookAuthors;

    public Author(String trim) {

        this.name = trim;
    }
}
