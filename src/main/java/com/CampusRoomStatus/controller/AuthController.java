package com.CampusRoomStatus.controller;

import com.CampusRoomStatus.integration.google.GoogleOAuthTokenService;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping
public class AuthController {

    private final GoogleOAuthTokenService tokenService;

    public AuthController(GoogleOAuthTokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Endpoint pour initier le processus d'authentification OAuth avec Google et
     * obtenir un refresh token.
     * Si un refresh token est déjà configuré, retourne une erreur 403 avec un
     * message explicatif.
     */
    @GetMapping("/get-token")
    public ResponseEntity<Void> getToken() {
        tokenService.validateNoRefreshTokenConfigured();

        return ResponseEntity.status(302)
                .header("Location", "/api/v1/oauth2/authorization/google")
                .build();
    }

    /**
     * Endpoint de callback pour récupérer le refresh token après une
     * authentification réussie avec Google.
     * Affiche le refresh token dans la console et le retourne dans la réponse.
     * Le serveur s'arrête automatiquement après 500ms pour éviter les problèmes de
     * sécurité liés à l'exposition du token.
     */
    @GetMapping("/callback")
    public Map<String, Object> callback(
            @AuthenticationPrincipal OidcUser user,
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient client) {

        String refreshToken = client.getRefreshToken() != null
                ? client.getRefreshToken().getTokenValue()
                : "NON DISPONIBLE";

        System.out.println("\n\n\n" +
                "======= COPIEZ CE REFRESH TOKEN DANS VOTRE .env =======\n\n" +
                "GOOGLE_REFRESH_TOKEN=" + refreshToken + "\n\n");

        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            System.exit(0);
        }).start();

        return Map.of(
                "email", user.getEmail(),
                "refreshToken", refreshToken);
    }
}