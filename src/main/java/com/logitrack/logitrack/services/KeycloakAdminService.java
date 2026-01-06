package com.logitrack.logitrack.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for interacting with Keycloak Admin API to manage users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.admin.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    /**
     * Get admin access token for Keycloak Admin API calls
     */
    private String getAdminAccessToken() {
        String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            return (String) response.getBody().get("access_token");
        } catch (HttpClientErrorException e) {
            log.error("Failed to get admin access token: {}", e.getMessage());
            throw new RuntimeException("Failed to authenticate with Keycloak Admin API", e);
        }
    }

    /**
     * Create a new user in Keycloak
     *
     * @param username User's username (typically email)
     * @param email User's email
     * @param firstName User's first name
     * @param lastName User's last name
     * @param password User's password
     * @param roles List of roles to assign to the user
     * @return Keycloak user ID
     */
    public String createUser(String username, String email, String firstName, String lastName, 
                           String password, List<String> roles) {
        String adminToken = getAdminAccessToken();
        String createUserUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        // Prepare user representation
        Map<String, Object> userRepresentation = new HashMap<>();
        userRepresentation.put("username", username);
        userRepresentation.put("email", email);
        userRepresentation.put("firstName", firstName);
        userRepresentation.put("lastName", lastName);
        userRepresentation.put("enabled", true);
        userRepresentation.put("emailVerified", true);

        // Set password credential directly in the user creation request
        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", password);
        credential.put("temporary", false);
        userRepresentation.put("credentials", List.of(credential));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userRepresentation, headers);

        try {
            // Create user
            ResponseEntity<String> response = restTemplate.postForEntity(createUserUrl, request, String.class);
            
            // Extract user ID from Location header
            String locationHeader = response.getHeaders().getLocation().toString();
            String userId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

            log.info("User created in Keycloak with ID: {}", userId);

            // The separate setUserPassword call is no longer needed

            // Assign roles to user
            if (roles != null && !roles.isEmpty()) {
                assignRolesToUser(userId, roles, adminToken);
            }

            return userId;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                log.error("User already exists in Keycloak: {}", username);
                throw new RuntimeException("User already exists in Keycloak");
            }
            log.error("Failed to create user in Keycloak: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to create user in Keycloak", e);
        }
    }



    private void assignRolesToUser(String userId, List<String> roleNames, String adminToken) {
        String rolesUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";

        // Get realm roles
        String realmRolesUrl = keycloakServerUrl + "/admin/realms/" + realm + "/roles";
        HttpHeaders getRolesHeaders = new HttpHeaders();
        getRolesHeaders.setBearerAuth(adminToken);
        HttpEntity<Void> getRolesRequest = new HttpEntity<>(getRolesHeaders);

        try {
            ResponseEntity<List> rolesResponse = restTemplate.exchange(
                    realmRolesUrl,
                    HttpMethod.GET,
                    getRolesRequest,
                    List.class
            );

            List<Map<String, Object>> allRoles = rolesResponse.getBody();
            List<Map<String, Object>> rolesToAssign = allRoles.stream()
                    .filter(role -> roleNames.contains(role.get("name").toString().toUpperCase()))
                    .toList();

            if (rolesToAssign.isEmpty()) {
                log.warn("No matching roles found to assign to user");
                return;
            }

            // Assign roles
            HttpHeaders assignRolesHeaders = new HttpHeaders();
            assignRolesHeaders.setContentType(MediaType.APPLICATION_JSON);
            assignRolesHeaders.setBearerAuth(adminToken);
            HttpEntity<List<Map<String, Object>>> assignRolesRequest = new HttpEntity<>(rolesToAssign, assignRolesHeaders);

            restTemplate.postForEntity(rolesUrl, assignRolesRequest, String.class);
            log.info("Roles assigned to user: {}", roleNames);

        } catch (Exception e) {
            log.error("Failed to assign roles to user: {}", e.getMessage());
            // Don't throw exception - user is created, just without roles
        }
    }


    public void deleteUser(String userId) {
        String adminToken = getAdminAccessToken();
        String deleteUserUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(deleteUserUrl, HttpMethod.DELETE, request, Void.class);
            log.info("User deleted from Keycloak: {}", userId);
        } catch (Exception e) {
            log.error("Failed to delete user from Keycloak: {}", e.getMessage());
        }
    }
}
