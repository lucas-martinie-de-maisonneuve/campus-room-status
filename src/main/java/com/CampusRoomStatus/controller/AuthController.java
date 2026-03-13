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

    @GetMapping("/api/v1/get-token")
    public Map<String, Object> me(
            @AuthenticationPrincipal OidcUser user,
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient client) {
        String refreshToken = client.getRefreshToken() != null
                ? client.getRefreshToken().getTokenValue()
                : "NON DISPONIBLE";

        Map<String, Object> response = Map.of(
                "email", user.getEmail(),
                "refreshToken", refreshToken);

        System.out.println("=== COPIEZ CE REFRESH TOKEN DANS VOTRE .env et relancer le projet ===");
        System.out.println("GOOGLE_REFRESH_TOKEN=" + refreshToken);
        System.out.println("=====================================================================");

        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            System.exit(0);
        }).start();

        return response;
    }
}