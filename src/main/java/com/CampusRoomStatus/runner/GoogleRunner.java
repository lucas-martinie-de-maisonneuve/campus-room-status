package com.CampusRoomStatus.runner;

import com.CampusRoomStatus.service.BuildingsService;
import com.CampusRoomStatus.service.RoomsService;
import com.CampusRoomStatus.service.EventsService;

import com.CampusRoomStatus.integration.google.GoogleOAuthTokenService;
import com.CampusRoomStatus.integration.google.GoogleDirectoryOAuthClient;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GoogleRunner implements CommandLineRunner {

    private final GoogleOAuthTokenService tokenService;
    private final BuildingsService buildingsService;
    private final RoomsService roomsService;
    private final EventsService eventsService;
    private final GoogleDirectoryOAuthClient googleDirectoryOAuthClient;


    public GoogleRunner(
            GoogleOAuthTokenService tokenService,
            BuildingsService buildingsService,
            RoomsService roomsService,
            EventsService eventsService,
            GoogleDirectoryOAuthClient googleDirectoryOAuthClient) {
        this.tokenService = tokenService;
        this.buildingsService = buildingsService;
        this.roomsService = roomsService;
        this.eventsService = eventsService;
        this.googleDirectoryOAuthClient = googleDirectoryOAuthClient;
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

        System.out.println("----- SYNC GOOGLE → POSTGRES -----");
        // buildingsService.syncFromGoogle();
        // System.out.println("Buildings synchronisés.");
        // roomsService.syncFromGoogle();
        // System.out.println("Rooms synchronisées.");
        // eventsService.syncAllRooms();

        System.out.println(">>> Appel 1 (doit appeler Google)");
        long t1 = System.currentTimeMillis();
        googleDirectoryOAuthClient.listBuildings();
        System.out.println(">>> Appel 1 terminé en " + (System.currentTimeMillis() - t1) + "ms");

        System.out.println(">>> Appel 2 (doit être instantané si cache actif)");
        long t2 = System.currentTimeMillis();
        googleDirectoryOAuthClient.listBuildings();
        System.out.println(">>> Appel 2 terminé en " + (System.currentTimeMillis() - t2) + "ms");

    }
}