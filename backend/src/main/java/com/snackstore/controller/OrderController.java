package com.snackstore.controller;

import com.snackstore.common.ApiResponse;
import com.snackstore.common.RequestContext;
import com.snackstore.service.OrderService;
import java.util.List;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping
  public ApiResponse<List<OrderService.OrderView>> list() {
    Long userId = RequestContext.getUserId();
    return ApiResponse.ok(orderService.listUserOrders(userId));
  }

  @PostMapping
  public ApiResponse<OrderService.OrderView> create(@RequestBody CreateOrderRequest req) {
    Long userId = RequestContext.getUserId();
    return ApiResponse.ok(orderService.createFromCart(userId, req.getAddressId()));
  }

  @GetMapping("/{id}")
  public ApiResponse<OrderService.OrderView> detail(@PathVariable("id") Long id) {
    Long userId = RequestContext.getUserId();
    return ApiResponse.ok(orderService.getUserOrder(userId, id));
  }

  @PostMapping("/{id}/cancel")
  public ApiResponse<Void> cancel(@PathVariable("id") Long id) {
    Long userId = RequestContext.getUserId();
    orderService.cancelUserOrder(userId, id);
    return ApiResponse.ok(null);
  }

  @PostMapping("/{id}/confirm")
  public ApiResponse<Void> confirm(@PathVariable("id") Long id) {
    Long userId = RequestContext.getUserId();
    orderService.confirmUserOrder(userId, id);
    return ApiResponse.ok(null);
  }

  @Data
  public static class CreateOrderRequest {
    private Long addressId;
  }
}

