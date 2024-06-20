package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;

@Getter
@Entity
@NoArgsConstructor
public class Publisher implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String name;

    @JsonCreator
    public Publisher(String name) {
        this.name = name;
    }
}
