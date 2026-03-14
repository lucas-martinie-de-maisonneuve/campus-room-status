package com.CampusRoomStatus.runner;

import com.CampusRoomStatus.integration.google.GoogleOAuthTokenService;
import com.CampusRoomStatus.service.BuildingsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GoogleRunner implements CommandLineRunner {

    private final GoogleOAuthTokenService tokenService;
    private final BuildingsService buildingsService;

    public GoogleRunner(GoogleOAuthTokenService tokenService, BuildingsService buildingsService) {
        this.tokenService = tokenService;
        this.buildingsService = buildingsService;
    }

    @Override
    public void run(String... args) {
        if (!tokenService.hasRefreshToken()) {
            System.out.println("==========================================================");
            System.out.println("Aucun refresh token configuré.");
            System.out.println("Connectez-vous sur http://localhost:8080/api/v1/get-token");
            System.out.println("puis renseignez GOOGLE_REFRESH_TOKEN dans votre .env");
            System.out.println("==========================================================");
            return;
        }

        System.out.println("----- SYNC GOOGLE vers POSTGRES -----");
        buildingsService.syncFromGoogle();
        System.out.println("Buildings synchronisés.");
    }
}