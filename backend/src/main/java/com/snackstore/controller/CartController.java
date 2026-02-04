package com.snackstore.controller;

import com.snackstore.common.ApiResponse;
import com.snackstore.common.RequestContext;
import com.snackstore.service.CartService;
import java.util.List;
import lombok.Data;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {
  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping
  public ApiResponse<List<CartService.CartItemView>> list() {
    Long userId = RequestContext.getUserId();
    return ApiResponse.ok(cartService.getCartItems(userId));
  }

  @PostMapping("/items")
  public ApiResponse<Void> add(@RequestBody AddItemRequest req) {
    Long userId = RequestContext.getUserId();
    cartService.addOrUpdate(userId, req.getProductId(), req.getQuantity());
    return ApiResponse.ok(null);
  }

  @PutMapping("/items/{id}")
  public ApiResponse<Void> update(@PathVariable("id") Long itemId, @RequestBody UpdateItemRequest req) {
    Long userId = RequestContext.getUserId();
    cartService.updateQuantity(userId, itemId, req.getQuantity());
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/items/{id}")
  public ApiResponse<Void> remove(@PathVariable("id") Long itemId) {
    Long userId = RequestContext.getUserId();
    cartService.removeItem(userId, itemId);
    return ApiResponse.ok(null);
  }

  @PostMapping("/clear")
  public ApiResponse<Void> clear() {
    Long userId = RequestContext.getUserId();
    cartService.clear(userId);
    return ApiResponse.ok(null);
  }

  @Data
  public static class AddItemRequest {
    private Long productId;
    private Integer quantity;
  }

  @Data
  public static class UpdateItemRequest {
    private Integer quantity;
  }
}

