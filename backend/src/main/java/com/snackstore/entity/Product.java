package com.snackstore.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product")
@Data
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "category_id", nullable = false)
  private Long categoryId;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(nullable = false)
  private Integer stock;

  @Column(nullable = false, length = 10)
  private String status;

  @Lob
  @Column(name = "main_image", columnDefinition = "MEDIUMTEXT")
  private String mainImage;

  @Column(length = 50)
  private String brand;

  @Column(name = "ship_from", length = 50)
  private String shipFrom;

  @Column(length = 50)
  private String origin;

  @Column(length = 50)
  private String sku;

  @Column(length = 1000)
  private String description;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
