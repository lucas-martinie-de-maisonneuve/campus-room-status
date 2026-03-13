package com.CampusRoomStatus.runner;

import com.CampusRoomStatus.integration.google.GoogleDirectoryOAuthClient;
import com.CampusRoomStatus.integration.google.GoogleOAuthTokenService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GoogleTestRunner implements CommandLineRunner {

    private final GoogleDirectoryOAuthClient client;

    public GoogleTestRunner(GoogleDirectoryOAuthClient client, GoogleOAuthTokenService tokenService) {
        this.client = client;
    }

    @Override
    public void run(String... args) {

        System.out.println("----- TEST GOOGLE DIRECTORY OAUTH -----");
        var buildings = client.listBuildings();
        var rooms = client.listRooms();
        System.out.println("Résultat : " + buildings + "\n\n\n" + rooms + "\n\n\n");
    }
}