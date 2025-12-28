package com.logitrack.logitrack.audit;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;

/**
 * Service for logging security-related audit events.
 * All logs are structured in JSON format for Elasticsearch/Kibana analysis.
 * 
 * CRITICAL: Never log passwords, tokens, or secrets!
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityAuditService {

    /**
     * Log successful authentication
     */
    public void logLoginSuccess(String email, String userId, String role) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event_type", "authentication_success");
        auditData.put("user_email", email);
        auditData.put("user_id", userId);
        auditData.put("user_role", role);
        auditData.put("action", "LOGIN");
        
        log.info("User logged in successfully", 
            StructuredArguments.entries(auditData));
    }

    /**
     * Log failed authentication attempt
     */
    public void logLoginFailure(String email, String reason) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event_type", "authentication_failure");
        auditData.put("user_email", email);
        auditData.put("reason", reason);
        auditData.put("action", "LOGIN_FAILED");
        
        log.warn("Login attempt failed", 
            StructuredArguments.entries(auditData));
    }

    /**
     * Log token refresh
     */
    public void logTokenRefresh(String userId, String email) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event_type", "token_refresh");
        auditData.put("user_id", userId);
        auditData.put("user_email", email);
        auditData.put("action", "TOKEN_REFRESH");
        
        log.info("Access token refreshed", 
            StructuredArguments.entries(auditData));
    }

    /**
     * Log authorization failure (403 Forbidden)
     */
    public void logAccessDenied(String endpoint, String method, String userId, String role) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event_type", "authorization_failure");
        auditData.put("endpoint", endpoint);
        auditData.put("http_method", method);
        auditData.put("user_id", userId);
        auditData.put("user_role", role);
        auditData.put("action", "ACCESS_DENIED");
        auditData.put("status_code", 403);
        
        log.warn("Access denied", 
            StructuredArguments.entries(auditData));
    }

    /**
     * Log unauthorized access attempt (401 Unauthorized)
     */
    public void logUnauthorizedAccess(String endpoint, String method, String reason) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event_type", "unauthorized_access");
        auditData.put("endpoint", endpoint);
        auditData.put("http_method", method);
        auditData.put("reason", reason);
        auditData.put("action", "UNAUTHORIZED_ACCESS");
        auditData.put("status_code", 401);
        
        log.warn("Unauthorized access attempt", 
            StructuredArguments.entries(auditData));
    }

    /**
     * Log user registration
     */
    public void logUserRegistration(String email, String userId, String role) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event_type", "user_registration");
        auditData.put("user_email", email);
        auditData.put("user_id", userId);
        auditData.put("user_role", role);
        auditData.put("action", "USER_REGISTERED");
        
        log.info("New user registered", 
            StructuredArguments.entries(auditData));
    }

    /**
     * Log logout event
     */
    public void logLogout(String userId, String email) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event_type", "logout");
        auditData.put("user_id", userId);
        auditData.put("user_email", email);
        auditData.put("action", "LOGOUT");
        
        log.info("User logged out", 
            StructuredArguments.entries(auditData));
    }

    /**
     * Log token expiration
     */
    public void logTokenExpired(String userId) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event_type", "token_expired");
        auditData.put("user_id", userId);
        auditData.put("action", "TOKEN_EXPIRED");
        
        log.info("Access token expired", 
            StructuredArguments.entries(auditData));
    }

    /**
     * Get current user info from SecurityContext
     */
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() ? auth.getName() : "anonymous";
    }
}
