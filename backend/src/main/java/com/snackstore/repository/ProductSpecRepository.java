package com.snackstore.repository;

import com.snackstore.entity.ProductSpec;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSpecRepository extends JpaRepository<ProductSpec, Long> {
  List<ProductSpec> findByProductId(Long productId);
}
