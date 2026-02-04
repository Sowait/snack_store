package com.snackstore.repository;

import com.snackstore.entity.OrderItem;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  List<OrderItem> findByOrderId(Long orderId);

  @Query(
      value =
          "SELECT oi.product_id "
              + "FROM order_item oi "
              + "JOIN orders o ON o.id = oi.order_id "
              + "JOIN product p ON p.id = oi.product_id "
              + "WHERE p.status = '上架' AND o.status <> '已取消' "
              + "GROUP BY oi.product_id "
              + "ORDER BY COUNT(DISTINCT oi.order_id) DESC, MAX(o.created_at) DESC, oi.product_id DESC",
      nativeQuery = true)
  List<Long> findTopFeaturedProductIds(Pageable pageable);
}
