package com.snackstore.entity;

import java.math.BigDecimal;
import javax.persistence.Lob;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "order_item")
@Data
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(name = "product_name", length = 100)
  private String productName;

  @Lob
  @Column(name = "product_image", columnDefinition = "TEXT")
  private String productImage;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(nullable = false)
  private Integer quantity;
}
