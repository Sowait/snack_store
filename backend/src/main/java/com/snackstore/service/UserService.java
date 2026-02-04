package com.snackstore.service;

import com.snackstore.common.BizException;
import com.snackstore.entity.User;
import com.snackstore.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getById(Long id) {
    return userRepository.findById(id).orElseThrow(() -> BizException.notFound("用户不存在"));
  }

  public List<User> listAll() {
    return userRepository.findAll();
  }

  @Transactional
  public User updateProfile(Long userId, String username, String email) {
    User user = getById(userId);
    if (username != null && !username.trim().isEmpty()) user.setUsername(username.trim());
    if (email != null) user.setEmail(email.trim());
    return userRepository.save(user);
  }

  @Transactional
  public User updateStatus(Long userId, String status) {
    User user = getById(userId);
    user.setStatus(status);
    return userRepository.save(user);
  }

  @Transactional
  public User updateByAdmin(Long userId, String username, String phone, String email, String status) {
    User user = getById(userId);

    if (username != null) {
      String v = username.trim();
      if (v.isEmpty()) throw BizException.badRequest("用户名不能为空");
      user.setUsername(v);
    }

    if (phone != null) {
      String v = phone.trim();
      if (v.isEmpty()) throw BizException.badRequest("手机号不能为空");
      if (!v.equals(user.getPhone())) {
        userRepository
            .findByPhone(v)
            .filter((u) -> !u.getId().equals(userId))
            .ifPresent((u) -> {
              throw BizException.badRequest("手机号已存在");
            });
      }
      user.setPhone(v);
    }

    if (email != null) {
      String v = email.trim();
      if (v.isEmpty()) {
        user.setEmail(null);
      } else {
        if (user.getEmail() == null || !v.equalsIgnoreCase(user.getEmail())) {
          userRepository
              .findByEmail(v)
              .filter((u) -> !u.getId().equals(userId))
              .ifPresent((u) -> {
                throw BizException.badRequest("邮箱已存在");
              });
        }
        user.setEmail(v);
      }
    }

    if (status != null && !status.trim().isEmpty()) {
      user.setStatus(status.trim());
    }

    return userRepository.save(user);
  }
}
