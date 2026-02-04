package com.snackstore.controller.admin;

import com.snackstore.common.ApiResponse;
import com.snackstore.service.AuthService;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {
  private final AuthService authService;

  public AdminAuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ApiResponse<AdminLoginResponse> login(@RequestBody AdminLoginRequest req) {
    AuthService.LoginResult result = authService.loginAdmin(req.getUsername(), req.getPassword());
    AdminLoginResponse resp = new AdminLoginResponse();
    resp.setToken(result.getToken());
    resp.setUsername(result.getUser().getUsername());
    resp.setAdminId(String.valueOf(result.getUser().getId()));
    return ApiResponse.ok(resp);
  }

  @Data
  public static class AdminLoginRequest {
    private String username;
    private String password;
  }

  @Data
  public static class AdminLoginResponse {
    private String token;
    private String adminId;
    private String username;
  }
}

