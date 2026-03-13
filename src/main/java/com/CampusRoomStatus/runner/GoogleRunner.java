package com.CampusRoomStatus.runner;

import com.CampusRoomStatus.integration.google.GoogleDirectoryOAuthClient;
import com.CampusRoomStatus.integration.google.GoogleOAuthTokenService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GoogleRunner implements CommandLineRunner {

    private final GoogleDirectoryOAuthClient client;
    private final GoogleOAuthTokenService tokenService;

    public GoogleRunner(GoogleDirectoryOAuthClient client, GoogleOAuthTokenService tokenService) {
        this.client = client;
        this.tokenService = tokenService;
    }

    @Override
    public void run(String... args) {
        if (!tokenService.hasRefreshToken()) {
            System.out.println("==========================================================");
            System.out.println("Aucun refresh token configuré.");
            System.out.println("Connectez-vous sur http://localhost:8080/api/v1/get-token,");
            System.out.println("puis renseignez GOOGLE_REFRESH_TOKEN dans votre .env");
            System.out.println("==========================================================");
            return;
        }

        System.out.println("----- TEST BUILDINGS / ROOMS -----");
        var buildings = client.listBuildings();
        var rooms = client.listRooms();
        System.out.println(" ========= Résultat ========= \n\n\n" + buildings + "\n\n\n" + rooms + "\n\n\n ==============================");
    }
}