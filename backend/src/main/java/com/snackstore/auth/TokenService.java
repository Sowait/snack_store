package com.snackstore.auth;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class TokenService {
  private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

  public String issueUserToken(Long userId) {
    String token = UUID.randomUUID().toString().replace("-", "");
    sessions.put(token, new Session(token, "USER", userId, Instant.now().toEpochMilli()));
    return token;
  }

  public String issueAdminToken(Long adminId) {
    String token = UUID.randomUUID().toString().replace("-", "");
    sessions.put(token, new Session(token, "ADMIN", adminId, Instant.now().toEpochMilli()));
    return token;
  }

  public Optional<Session> find(String token) {
    if (token == null) return Optional.empty();
    return Optional.ofNullable(sessions.get(token));
  }

  public void revoke(String token) {
    if (token == null) return;
    sessions.remove(token);
  }

  @Data
  @AllArgsConstructor
  public static class Session {
    private String token;
    private String type;
    private Long subjectId;
    private Long issuedAtMs;
  }
}

