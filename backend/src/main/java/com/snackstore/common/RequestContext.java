package com.snackstore.common;

public class RequestContext {
  private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
  private static final ThreadLocal<Long> ADMIN_ID = new ThreadLocal<>();

  public static void setUserId(Long userId) {
    USER_ID.set(userId);
  }

  public static Long getUserId() {
    return USER_ID.get();
  }

  public static void clearUserId() {
    USER_ID.remove();
  }

  public static void setAdminId(Long adminId) {
    ADMIN_ID.set(adminId);
  }

  public static Long getAdminId() {
    return ADMIN_ID.get();
  }

  public static void clearAdminId() {
    ADMIN_ID.remove();
  }

  public static void clearAll() {
    clearUserId();
    clearAdminId();
  }
}

