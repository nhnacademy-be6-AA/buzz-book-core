package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Table(name = "author")
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(length = 10,nullable = false)
    private String role;

    @OneToMany(mappedBy = "author")
    private List<BookAuthor> bookAuthors;

}
