package com.snackstore.auth;

import com.snackstore.common.BizException;
import com.snackstore.common.RequestContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
  private final TokenService tokenService;

  private static final Set<String> USER_PUBLIC_PREFIX =
      Collections.unmodifiableSet(
          new HashSet<>(Arrays.asList("/api/auth", "/api/products", "/api/categories")));

  private static final Set<String> ADMIN_PUBLIC_PREFIX =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("/api/admin/auth")));

  public AuthInterceptor(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    RequestContext.clearAll();
    String path = request.getRequestURI();

    if (path.startsWith("/api/admin")) {
      if (ADMIN_PUBLIC_PREFIX.stream().anyMatch(p -> path.equals(p) || path.startsWith(p + "/"))) {
        return true;
      }
      String token = extractToken(request);
      TokenService.Session session =
          tokenService.find(token).orElseThrow(() -> BizException.unauthorized("未登录"));
      if (!"ADMIN".equals(session.getType())) {
        throw BizException.forbidden("无权限");
      }
      RequestContext.setAdminId(session.getSubjectId());
      return true;
    }

    if (path.startsWith("/api")) {
      if (USER_PUBLIC_PREFIX.stream().anyMatch(p -> path.equals(p) || path.startsWith(p + "/"))) {
        return true;
      }
      String token = extractToken(request);
      TokenService.Session session =
          tokenService.find(token).orElseThrow(() -> BizException.unauthorized("未登录"));
      if (!"USER".equals(session.getType())) {
        throw BizException.forbidden("无权限");
      }
      RequestContext.setUserId(session.getSubjectId());
      return true;
    }

    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    RequestContext.clearAll();
  }

  private String extractToken(HttpServletRequest request) {
    String auth = request.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      return auth.substring("Bearer ".length()).trim();
    }
    String token = request.getHeader("X-Token");
    if (token != null && !token.trim().isEmpty()) return token.trim();
    return null;
  }
}
