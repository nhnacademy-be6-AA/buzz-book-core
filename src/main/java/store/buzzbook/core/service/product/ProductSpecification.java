package store.buzzbook.core.service.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSpecification {

	private final CategoryService categoryService;

	public Specification<Product> orderBy(String orderBy) {
		return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
			if ("name".equals(orderBy)) {
				query.orderBy(criteriaBuilder.asc(root.get("productName")));
			} else if ("score".equals(orderBy)) {
				query.orderBy(criteriaBuilder.desc(root.get("score")));
			} else if ("reviews".equals(orderBy)) {
				query.orderBy(criteriaBuilder.desc(root.get("reviews")));
			} else {
				query.orderBy(criteriaBuilder.asc(root.get("id")));
			}
			return query.getRestriction();
		};
	}

	public Specification<Product> getProductsByCriteria(Product.StockStatus status, String name, Integer categoryId) {
		return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (status != null) {
				predicates.add(criteriaBuilder.equal(root.get("stockStatus"), status));
			}
			if (name != null && !name.isBlank()) {
				predicates.add(criteriaBuilder.like(root.get("productName"), "%" + name + "%"));
			}
			if (categoryId != null) {
				List<Integer> categoryIds = categoryService.findAllSubcategories(categoryId).stream().map(Category::getId).toList();
				predicates.add(root.get("category").get("id").in(categoryIds));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
