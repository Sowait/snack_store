package com.snackstore.service;

import com.snackstore.auth.TokenService;
import com.snackstore.common.BizException;
import com.snackstore.entity.AdminUser;
import com.snackstore.entity.User;
import com.snackstore.repository.AdminUserRepository;
import com.snackstore.repository.UserRepository;
import com.snackstore.util.PasswordUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final AdminUserRepository adminUserRepository;
  private final TokenService tokenService;

  public AuthService(
      UserRepository userRepository, AdminUserRepository adminUserRepository, TokenService tokenService) {
    this.userRepository = userRepository;
    this.adminUserRepository = adminUserRepository;
    this.tokenService = tokenService;
  }

  @Transactional
  public User register(String username, String phone, String email, String password) {
    if (username == null || username.trim().isEmpty()) throw BizException.badRequest("用户名不能为空");
    if (phone == null || phone.trim().isEmpty()) throw BizException.badRequest("手机号不能为空");
    if (password == null || password.trim().isEmpty()) throw BizException.badRequest("密码不能为空");

    Optional<User> existed = userRepository.findByPhone(phone.trim());
    if (existed.isPresent()) throw BizException.badRequest("手机号已注册");

    User user = new User();
    user.setUsername(username.trim());
    user.setPhone(phone.trim());
    user.setEmail(email == null ? null : email.trim());
    user.setPasswordHash(PasswordUtil.sha256(password));
    user.setStatus("启用");
    user.setCreatedAt(LocalDateTime.now());
    return userRepository.save(user);
  }

  public LoginResult loginUser(String phone, String password) {
    if (phone == null || phone.trim().isEmpty()) throw BizException.badRequest("手机号不能为空");
    if (password == null || password.trim().isEmpty()) throw BizException.badRequest("密码不能为空");
    User user =
        userRepository.findByPhone(phone.trim()).orElseThrow(() -> BizException.unauthorized("账号或密码错误"));
    if (!"启用".equals(user.getStatus())) throw BizException.forbidden("账号已被禁用");
    if (!PasswordUtil.matches(password, user.getPasswordHash())) throw BizException.unauthorized("账号或密码错误");
    String token = tokenService.issueUserToken(user.getId());
    return new LoginResult(token, user);
  }

  public LoginResult loginAdmin(String username, String password) {
    if (username == null || username.trim().isEmpty()) throw BizException.badRequest("账号不能为空");
    if (password == null || password.trim().isEmpty()) throw BizException.badRequest("密码不能为空");
    AdminUser admin =
        adminUserRepository
            .findByUsername(username.trim())
            .orElseThrow(() -> BizException.unauthorized("账号或密码错误"));
    if (!"启用".equals(admin.getStatus())) throw BizException.forbidden("账号已被禁用");
    if (!PasswordUtil.matches(password, admin.getPasswordHash())) throw BizException.unauthorized("账号或密码错误");
    String token = tokenService.issueAdminToken(admin.getId());
    User fakeUser = new User();
    fakeUser.setId(admin.getId());
    fakeUser.setUsername(admin.getUsername());
    fakeUser.setStatus(admin.getStatus());
    return new LoginResult(token, fakeUser);
  }

  public void logout(String token) {
    tokenService.revoke(token);
  }

  public static class LoginResult {
    private final String token;
    private final User user;

    public LoginResult(String token, User user) {
      this.token = token;
      this.user = user;
    }

    public String getToken() {
      return token;
    }

    public User getUser() {
      return user;
    }
  }
}

