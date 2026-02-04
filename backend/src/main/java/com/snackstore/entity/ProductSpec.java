package com.snackstore.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product_spec")
@Data
public class ProductSpec {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(name = "spec_key", nullable = false, length = 50)
  private String specKey;

  @Column(name = "spec_value", nullable = false, length = 100)
  private String specValue;
}
