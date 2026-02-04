package com.snackstore.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_no", nullable = false, length = 32)
  private String orderNo;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 20)
  private String status;

  @Column(name = "receiver_name", length = 50)
  private String receiverName;

  @Column(name = "receiver_phone", length = 20)
  private String receiverPhone;

  @Column(name = "receiver_region", length = 100)
  private String receiverRegion;

  @Column(name = "receiver_detail", length = 200)
  private String receiverDetail;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
