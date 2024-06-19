package store.buzzbook.core.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.core.entity.product.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher,Long> {

    public Publisher findByName(String name);
}
