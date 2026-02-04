package com.snackstore.repository;

import com.snackstore.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByPhone(String phone);
  Optional<User> findByEmail(String email);
}
