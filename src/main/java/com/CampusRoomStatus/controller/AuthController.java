package com.CampusRoomStatus.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> me(
        @AuthenticationPrincipal OidcUser user,
        @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient client
    ) {
        String refreshToken = client.getRefreshToken() != null
                ? client.getRefreshToken().getTokenValue()
                : "NON DISPONIBLE";

        System.out.println("=== COPIEZ CE REFRESH TOKEN DANS VOTRE .env et relancer le projet ===");
        System.out.println("GOOGLE_REFRESH_TOKEN=" + refreshToken);
        System.out.println("===============================================");

        return Map.of(
            "email", user.getEmail(),
            "refreshToken", refreshToken
        );
    }
}