package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.core.entity.product.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);
    void deleteById(int id);

    // parentCategoryId값이 null인 모든 카테고리
    // = 모든 최상위 카테고리
    List<Category> findAllByParentCategoryIsNull();

    // List<id>로 1차 하위 카테고리 찾기
    // = 매개변수(parentCategoryId)를 parentCategoryId로 가지고 있는 모든 1차 하위카테고리
    // = 해당카테고리 하위의 하위의 하위의... 모든 카테고리 아님!!!
    List<Category> findAllByParentCategoryIdIn(List<Integer> categoryIds);

    // List<id>로 존재하는 카테고리 개수 조회
    long countByIdIn(List<Integer> categoryIds);

    // List<id>로 카테고리들 조회
    // 없는 카테고리 id가 있으면 제외하고 나머지들 반환
    List<Category> findAllByIdIn(List<Integer> categoryIds);

}
