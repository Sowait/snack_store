package com.snackstore.repository;

import com.snackstore.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findByStatusOrderBySortOrderAsc(String status);
}
