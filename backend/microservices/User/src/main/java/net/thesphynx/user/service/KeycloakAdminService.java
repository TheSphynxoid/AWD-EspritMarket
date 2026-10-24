package net.thesphynx.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakAdminService {

    private final RestClient restClient;

    @Value("${keycloak.auth-server-url:http://localhost:8080}")
    private String serverUrl;

    @Value("${keycloak.realm:espritmarket}")
    private String realm;

    @Value("${keycloak.admin-username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin-password:admin}")
    private String adminPassword;

    public KeycloakAdminService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public void register(String username, String email, String password) {
        String token = adminToken();

        Map<String, Object> user = Map.of(
            "username", username,
            "email", email,
            "enabled", true,
            "credentials", List.of(Map.of(
                "type", "password",
                "value", password,
                "temporary", false
            ))
        );

        restClient.post()
            .uri(serverUrl + "/admin/realms/" + realm + "/users")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .body(user)
            .retrieve()
            .toBodilessEntity();
    }

    private String adminToken() {
        Map<String, String> form = Map.of(
            "grant_type", "password",
            "client_id", "admin-cli",
            "username", adminUsername,
            "password", adminPassword
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
            .uri(serverUrl + "/realms/master/protocol/openid-connect/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(toForm(form))
            .retrieve()
            .body(Map.class);

        return (String) response.get("access_token");
    }

    private String toForm(Map<String, String> form) {
        StringBuilder sb = new StringBuilder();
        for (var e : form.entrySet()) {
            if (sb.length() > 0) sb.append("&");
            sb.append(e.getKey()).append("=").append(e.getValue());
        }
        return sb.toString();
    }
}
