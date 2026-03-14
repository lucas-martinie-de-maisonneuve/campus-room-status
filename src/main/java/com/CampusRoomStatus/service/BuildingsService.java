package com.CampusRoomStatus.service;

import com.CampusRoomStatus.dto.BuildingDTO;
import com.CampusRoomStatus.entity.Building;
import com.CampusRoomStatus.integration.google.GoogleDirectoryOAuthClient;
import com.CampusRoomStatus.mapper.BuildingMapper;
import com.CampusRoomStatus.repository.BuildingsRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BuildingsService {

    private final BuildingsRepository buildingsRepository;
    private final GoogleDirectoryOAuthClient googleDirectoryOAuthClient;
    private final BuildingMapper buildingMapper;

    public BuildingsService(
            BuildingsRepository buildingsRepository,
            GoogleDirectoryOAuthClient googleDirectoryOAuthClient,
            BuildingMapper buildingMapper) {
        this.buildingsRepository = buildingsRepository;
        this.googleDirectoryOAuthClient = googleDirectoryOAuthClient;
        this.buildingMapper = buildingMapper;
    }

    /**
     * Récupère la liste de tous les bâtiments disponibles dans la base de données.
     * Chaque bâtiment est converti en BuildingDTO avant d'être retourné.
     * @return
     */
    public List<BuildingDTO> getAll() {
        return buildingsRepository.findAll()
                .stream()
                .map(buildingMapper::toDTO)
                .toList();
    }

    /**
     * Récupère les détails d'un bâtiment en utilisant son ID interne (ID de la base
     * de données).
     * Si le bâtiment n'est pas trouvé, retourne une Optional vide.
     * @param id
     * @return
     */
    public Optional<BuildingDTO> getById(Integer id) {
        return buildingsRepository.findById(id)
                .map(buildingMapper::toDTO);
    }

    /**
     * Récupère les détails d'un bâtiment en utilisant son ID Google.
     * Si le bâtiment n'est pas trouvé, retourne une Optional vide.
     * @param buildingGoogleId
     * @return
     */
    public Optional<BuildingDTO> getByGoogleId(String buildingGoogleId) {
        return buildingsRepository.findByBuildingGoogleId(buildingGoogleId)
                .map(buildingMapper::toDTO);
    }

    /**
     * Synchronise les bâtiments de la base de données avec ceux de Google
     * Directory.
     * Récupère la liste des bâtiments depuis Google Directory et met à jour ou crée
     * les enregistrements correspondants dans la base de données PostgreSQL.
     */
    @SuppressWarnings("unchecked")
    public void syncFromGoogle() {
        Map<String, Object> response = googleDirectoryOAuthClient.listBuildings();

        if (response == null || !response.containsKey("buildings")) {
            return;
        }

        List<Map<String, Object>> googleBuildings = (List<Map<String, Object>>) response.get("buildings");

        for (Map<String, Object> googleBuilding : googleBuildings) {
            String googleId = (String) googleBuilding.get("buildingId");

            Building building = buildingsRepository.findByBuildingGoogleId(googleId)
                    .orElse(new Building());

            building.setBuildingGoogleId(googleId);
            building.setBuildingName((String) googleBuilding.get("buildingName"));
            building.setBuildingFloors((List<String>) googleBuilding.get("floorNames"));

            Map<String, Object> address = (Map<String, Object>) googleBuilding.get("address");
            if (address != null) {
                Map<String, String> addressMap = new HashMap<>();

                List<String> addressLines = (List<String>) address.get("addressLines");
                if (addressLines != null)
                    addressMap.put("address", String.join(", ", addressLines));

                String city = (String) address.get("locality");
                if (city != null)
                    addressMap.put("city", city);

                String postalCode = (String) address.get("postalCode");
                if (postalCode != null)
                    addressMap.put("postalCode", postalCode);

                building.setBuildingAddress(addressMap);
            }

            buildingsRepository.save(building);
        }
    }
}