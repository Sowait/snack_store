package com.snackstore.controller;

import com.snackstore.common.ApiResponse;
import com.snackstore.common.RequestContext;
import com.snackstore.entity.User;
import com.snackstore.service.UserService;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ApiResponse<User> me() {
    Long userId = RequestContext.getUserId();
    return ApiResponse.ok(userService.getById(userId));
  }

  @PutMapping
  public ApiResponse<User> update(@RequestBody UpdateProfileRequest req) {
    Long userId = RequestContext.getUserId();
    return ApiResponse.ok(userService.updateProfile(userId, req.getUsername(), req.getEmail()));
  }

  @Data
  public static class UpdateProfileRequest {
    private String username;
    private String email;
  }
}

