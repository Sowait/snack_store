package com.snackstore.repository;

import com.snackstore.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByStatusOrderByCreatedAtDesc(String status);
  List<Product> findByCategoryIdAndStatusOrderByCreatedAtDesc(Long categoryId, String status);
  List<Product> findByStatusAndNameContainingOrderByCreatedAtDesc(String status, String keyword);
  List<Product> findByCategoryIdAndStatusAndNameContainingOrderByCreatedAtDesc(
      Long categoryId, String status, String keyword);
}
