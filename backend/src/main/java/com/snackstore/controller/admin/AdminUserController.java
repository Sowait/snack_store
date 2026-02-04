package com.snackstore.controller.admin;

import com.snackstore.common.ApiResponse;
import com.snackstore.entity.User;
import com.snackstore.service.UserService;
import java.util.List;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
  private final UserService userService;

  public AdminUserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ApiResponse<List<User>> list() {
    return ApiResponse.ok(userService.listAll());
  }

  @PutMapping("/{id}/status")
  public ApiResponse<User> updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest req) {
    return ApiResponse.ok(userService.updateStatus(id, req.getStatus()));
  }

  @PutMapping("/{id}")
  public ApiResponse<User> update(@PathVariable("id") Long id, @RequestBody UpdateUserRequest req) {
    return ApiResponse.ok(
        userService.updateByAdmin(id, req.getUsername(), req.getPhone(), req.getEmail(), req.getStatus()));
  }

  @Data
  public static class UpdateStatusRequest {
    private String status;
  }

  @Data
  public static class UpdateUserRequest {
    private String username;
    private String phone;
    private String email;
    private String status;
  }
}
