package com.CampusRoomStatus.integration.google;

import com.CampusRoomStatus.config.AppProperties;
import com.CampusRoomStatus.exception.ApiException;
import com.CampusRoomStatus.exception.ErrorCode;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class GoogleDirectoryOAuthClient {

    private final GoogleOAuthTokenService tokenService;
    private final AppProperties properties;
    private final RestClient restClient;

    private static final String BASE_URL = "https://admin.googleapis.com/admin/directory/v1";

    public GoogleDirectoryOAuthClient(GoogleOAuthTokenService tokenService, AppProperties properties) {
        this.tokenService = tokenService;
        this.properties = properties;
        this.restClient = RestClient.create();
    }

    /**
     * Récupère la liste des bâtiments depuis l'API Google Directory en utilisant le
     * token d'accès OAuth.
     * Le token d'accès est obtenu via le GoogleOAuthTokenService, qui gère le
     * rafraîchissement du token si nécessaire.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> listBuildings() {
        String token = tokenService.getAccessToken();
        try {
            return restClient.get()
                    .uri(BASE_URL + "/customer/" + properties.getCustomer() + "/resources/buildings")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE,"Impossible de récupérer les bâtiments");
        }
    }

    /**
     * Récupère la liste des salles depuis l'API Google Directory en utilisant le
     * token d'accès OAuth.
     * Le token d'accès est obtenu via le GoogleOAuthTokenService, qui gère le
     * rafraîchissement du token si nécessaire.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> listRooms() {
        String token = tokenService.getAccessToken();

        try {
            return restClient.get()
                    .uri(BASE_URL + "/customer/" + properties.getCustomer() + "/resources/calendars")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE,"Impossible de récupérer les salles");
        }
    }
}