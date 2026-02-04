package com.snackstore.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "shipment")
@Data
public class Shipment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(length = 50)
  private String company;

  @Column(name = "tracking_no", length = 50)
  private String trackingNo;

  @Column(name = "shipped_at")
  private LocalDateTime shippedAt;
}
