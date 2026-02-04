package com.snackstore.controller;

import com.snackstore.common.ApiResponse;
import com.snackstore.service.AuthService;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ApiResponse<LoginResponse> register(@RequestBody RegisterRequest req) {
    authService.register(req.getUsername(), req.getPhone(), req.getEmail(), req.getPassword());
    AuthService.LoginResult result = authService.loginUser(req.getPhone(), req.getPassword());
    return ApiResponse.ok(LoginResponse.from(result));
  }

  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@RequestBody LoginRequest req) {
    AuthService.LoginResult result = authService.loginUser(req.getPhone(), req.getPassword());
    return ApiResponse.ok(LoginResponse.from(result));
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
    String token = null;
    if (authorization != null && authorization.startsWith("Bearer ")) {
      token = authorization.substring("Bearer ".length()).trim();
    }
    authService.logout(token);
    return ApiResponse.ok(null);
  }

  @Data
  public static class RegisterRequest {
    private String username;
    private String phone;
    private String email;
    private String password;
  }

  @Data
  public static class LoginRequest {
    private String phone;
    private String password;
  }

  @Data
  public static class LoginResponse {
    private String token;
    private UserProfile user;

    public static LoginResponse from(AuthService.LoginResult result) {
      LoginResponse resp = new LoginResponse();
      resp.setToken(result.getToken());
      UserProfile profile = new UserProfile();
      profile.setId(String.valueOf(result.getUser().getId()));
      profile.setUsername(result.getUser().getUsername());
      profile.setPhone(result.getUser().getPhone());
      profile.setEmail(result.getUser().getEmail());
      profile.setStatus(result.getUser().getStatus());
      resp.setUser(profile);
      return resp;
    }
  }

  @Data
  public static class UserProfile {
    private String id;
    private String username;
    private String phone;
    private String email;
    private String status;
  }
}

