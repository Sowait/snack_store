package com.snackstore.repository;

import com.snackstore.entity.ProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
  List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);
}
