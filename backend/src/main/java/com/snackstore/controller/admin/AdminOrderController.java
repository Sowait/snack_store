package com.snackstore.controller.admin;

import com.snackstore.common.ApiResponse;
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
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
  private final OrderService orderService;

  public AdminOrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping
  public ApiResponse<List<OrderService.OrderView>> list() {
    return ApiResponse.ok(orderService.listAllOrders());
  }

  @PostMapping("/{id}/ship")
  public ApiResponse<Void> ship(@PathVariable("id") Long id, @RequestBody ShipRequest req) {
    orderService.shipOrder(id, req.getCompany(), req.getTrackingNo());
    return ApiResponse.ok(null);
  }

  @PostMapping("/{id}/status")
  public ApiResponse<Void> updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest req) {
    orderService.updateOrderStatus(id, req.getStatus());
    return ApiResponse.ok(null);
  }

  @Data
  public static class ShipRequest {
    private String company;
    private String trackingNo;
  }

  @Data
  public static class UpdateStatusRequest {
    private String status;
  }
}

