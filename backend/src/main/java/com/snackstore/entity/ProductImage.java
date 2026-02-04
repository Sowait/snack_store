package com.snackstore.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product_image")
@Data
public class ProductImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Lob
  @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
  private String url;

  @Column(name = "sort_order")
  private Integer sortOrder;
}
