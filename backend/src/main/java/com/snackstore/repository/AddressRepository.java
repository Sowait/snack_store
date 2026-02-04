package com.snackstore.repository;

import com.snackstore.entity.Address;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
  List<Address> findByUserId(Long userId);
}
