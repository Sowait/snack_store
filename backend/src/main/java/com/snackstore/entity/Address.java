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
@Table(name = "address")
@Data
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(nullable = false, length = 50)
  private String name;

  @Column(nullable = false, length = 20)
  private String phone;

  @Column(nullable = false, length = 100)
  private String region;

  @Column(nullable = false, length = 200)
  private String detail;

  @Column(name = "is_default")
  private Boolean isDefault;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
