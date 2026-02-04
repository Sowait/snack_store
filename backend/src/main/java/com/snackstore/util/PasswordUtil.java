package com.snackstore.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {
  private PasswordUtil() {}

  public static String sha256(String raw) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] bytes = digest.digest(String.valueOf(raw).getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : bytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public static boolean matches(String raw, String stored) {
    if (stored == null) return false;
    String s = stored.trim();
    if (s.matches("^[0-9a-fA-F]{64}$")) {
      return sha256(raw).equalsIgnoreCase(s);
    }
    return String.valueOf(raw).equals(s);
  }
}

