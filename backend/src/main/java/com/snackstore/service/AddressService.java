package com.snackstore.service;

import com.snackstore.common.BizException;
import com.snackstore.entity.Address;
import com.snackstore.repository.AddressRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {
  private final AddressRepository addressRepository;

  public AddressService(AddressRepository addressRepository) {
    this.addressRepository = addressRepository;
  }

  public List<Address> list(Long userId) {
    return addressRepository.findByUserId(userId);
  }

  @Transactional
  public Address create(Long userId, Address address) {
    address.setId(null);
    address.setUserId(userId);
    address.setCreatedAt(LocalDateTime.now());
    if (address.getIsDefault() == null) address.setIsDefault(false);
    Address saved = addressRepository.save(address);
    if (Boolean.TRUE.equals(saved.getIsDefault())) {
      setDefault(userId, saved.getId());
      return addressRepository.findById(saved.getId()).orElse(saved);
    }
    return saved;
  }

  @Transactional
  public Address update(Long userId, Long id, Address address) {
    Address existed = addressRepository.findById(id).orElseThrow(() -> BizException.notFound("地址不存在"));
    if (!existed.getUserId().equals(userId)) throw BizException.forbidden("无权限");
    existed.setName(address.getName());
    existed.setPhone(address.getPhone());
    existed.setRegion(address.getRegion());
    existed.setDetail(address.getDetail());
    if (address.getIsDefault() != null) {
      existed.setIsDefault(address.getIsDefault());
    }
    Address saved = addressRepository.save(existed);
    if (Boolean.TRUE.equals(saved.getIsDefault())) {
      setDefault(userId, saved.getId());
      return addressRepository.findById(saved.getId()).orElse(saved);
    }
    return saved;
  }

  @Transactional
  public void delete(Long userId, Long id) {
    Address existed = addressRepository.findById(id).orElseThrow(() -> BizException.notFound("地址不存在"));
    if (!existed.getUserId().equals(userId)) throw BizException.forbidden("无权限");
    addressRepository.delete(existed);
  }

  @Transactional
  public void setDefault(Long userId, Long id) {
    List<Address> list = addressRepository.findByUserId(userId);
    for (Address a : list) {
      boolean isDefault = a.getId().equals(id);
      if (a.getIsDefault() != null && a.getIsDefault().equals(isDefault)) continue;
      a.setIsDefault(isDefault);
      addressRepository.save(a);
    }
  }
}

