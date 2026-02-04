package com.snackstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String username;

  @Column(nullable = false, length = 20)
  private String phone;

  @Column(length = 100)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 100)
  @JsonIgnore
  private String passwordHash;

  @Column(nullable = false, length = 10)
  private String status;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
