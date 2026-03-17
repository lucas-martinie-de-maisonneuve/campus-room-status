package com.CampusRoomStatus.integration.google;

import com.CampusRoomStatus.config.AppProperties;
import com.CampusRoomStatus.exception.ApiException;
import com.CampusRoomStatus.exception.ErrorCode;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class GoogleOAuthTokenService {

    private final AppProperties properties;
    private final RestClient restClient;

    public GoogleOAuthTokenService(AppProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.create();
    }

    public void validateNoRefreshTokenConfigured() {
        if (hasRefreshToken()) {
            throw new ApiException(
                    ErrorCode.FORBIDDEN,
                    "Refresh token déjà configuré. Supprimez GOOGLE_REFRESH_TOKEN pour en configurer un nouveau."
            );
        }
    }

    /**
     * Vérifie si un refresh token est configuré dans les propriétés de
     * l'application.
     * Un refresh token est nécessaire pour obtenir un token d'accès valide pour les
     * appels à l'API Google Directory.
     * Retourne true si un refresh token est configuré, false sinon.
     */
    public boolean hasRefreshToken() {
        return properties.getOauthRefreshToken() != null && !properties.getOauthRefreshToken().isBlank();
    }

    /**
     * Obtient un token d'accès valide en utilisant le refresh token configuré. Si
     * aucun refresh token n'est configuré, lance une exception.
     * Le token d'accès est nécessaire pour authentifier les requêtes vers l'API
     * Google Directory. Cette méthode gère également le rafraîchissement du token
     * si nécessaire.
     */
    public String getAccessToken() {
        if (!hasRefreshToken()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "Aucun refresh token configuré. Connectez-vous sur http://localhost:8080/api/v1/get-token");
        }

        var body = new LinkedMultiValueMap<String, String>();
        body.add("client_id", properties.getOauthClientId());
        body.add("client_secret", properties.getOauthClientSecret());
        body.add("refresh_token", properties.getOauthRefreshToken());
        body.add("grant_type", "refresh_token");

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("https://oauth2.googleapis.com/token")
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            return (String) response.get("access_token");
        } catch (Exception e) {
            throw new ApiException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE,"Impossible de rafraîchir le token OAuth");
        }
    }
}