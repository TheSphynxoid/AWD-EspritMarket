package net.thesphynx.srv.security;

import org.springframework.security.oauth2.jwt.Jwt;

/** Utilisateur connecté, extrait du JWT Keycloak. */
public record CurrentUser(String id, String name, String email) {

    public static CurrentUser from(Jwt jwt) {
        String name = jwt.getClaimAsString("name");
        if (name == null || name.isBlank()) name = jwt.getClaimAsString("preferred_username");
        if (name == null || name.isBlank()) name = "Utilisateur";
        return new CurrentUser(jwt.getSubject(), name, jwt.getClaimAsString("email"));
    }
}
