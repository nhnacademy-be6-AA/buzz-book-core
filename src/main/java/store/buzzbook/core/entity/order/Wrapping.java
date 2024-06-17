package store.buzzbook.core.entity.order;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Wrapping {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String paper;
	private int price;

	@OneToMany(mappedBy = "wrapping", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<OrderDetail> orderDetails;
}
