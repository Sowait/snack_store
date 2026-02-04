package com.snackstore.repository;

import com.snackstore.entity.CartItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByCartId(Long cartId);
  List<CartItem> findByCartIdOrderByCreatedAtDesc(Long cartId);
}
