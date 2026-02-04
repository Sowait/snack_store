package com.snackstore.controller;

import com.snackstore.common.ApiResponse;
import com.snackstore.common.RequestContext;
import com.snackstore.entity.Address;
import com.snackstore.service.AddressService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
  private final AddressService addressService;

  public AddressController(AddressService addressService) {
    this.addressService = addressService;
  }

  @GetMapping
  public ApiResponse<List<Address>> list() {
    Long userId = RequestContext.getUserId();
    return ApiResponse.ok(addressService.list(userId));
  }

  @PostMapping
  public ApiResponse<Address> create(@RequestBody Address address) {
    Long userId = RequestContext.getUserId();
    return ApiResponse.ok(addressService.create(userId, address));
  }

  @PutMapping("/{id}")
  public ApiResponse<Address> update(@PathVariable("id") Long id, @RequestBody Address address) {
    Long userId = RequestContext.getUserId();
    return ApiResponse.ok(addressService.update(userId, id, address));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable("id") Long id) {
    Long userId = RequestContext.getUserId();
    addressService.delete(userId, id);
    return ApiResponse.ok(null);
  }

  @PostMapping("/{id}/default")
  public ApiResponse<Void> setDefault(@PathVariable("id") Long id) {
    Long userId = RequestContext.getUserId();
    addressService.setDefault(userId, id);
    return ApiResponse.ok(null);
  }
}

