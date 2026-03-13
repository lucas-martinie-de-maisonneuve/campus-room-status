package com.CampusRoomStatus.integration.google;

import com.CampusRoomStatus.config.AppProperties;
import com.CampusRoomStatus.exception.GoogleIntegrationException;
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

    public boolean hasRefreshToken() {
        String token = properties.getOauthRefreshToken();
        return token != null && !token.isBlank();
    }

    public String getAccessToken() {
        if (!hasRefreshToken()) {
            throw new GoogleIntegrationException(
                    "Aucun refresh token configuré. Connectez-vous sur http://localhost:8080//api/v1/get-token", null);
        }

        var body = new LinkedMultiValueMap<String, String>();
        body.add("client_id", properties.getOauthClientId());
        body.add("client_secret", properties.getOauthClientSecret());
        body.add("refresh_token", properties.getOauthRefreshToken());
        body.add("grant_type", "refresh_token");

        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> response = restClient.post()
                    .uri("https://oauth2.googleapis.com/token")
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            return (String) response.get("access_token");
        } catch (Exception e) {
            throw new GoogleIntegrationException("Impossible de rafraîchir le token OAuth : " + e.getMessage(), e);
        }
    }
}