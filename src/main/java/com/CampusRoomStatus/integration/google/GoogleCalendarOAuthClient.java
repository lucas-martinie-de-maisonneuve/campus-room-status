package com.CampusRoomStatus.integration.google;

import com.CampusRoomStatus.exception.GoogleIntegrationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.Map;

@Component
public class GoogleCalendarOAuthClient {

    private final GoogleOAuthTokenService tokenService;
    private final RestClient restClient;

    private static final String BASE_URL = "https://www.googleapis.com/calendar/v3";

    public GoogleCalendarOAuthClient(GoogleOAuthTokenService tokenService) {
        this.tokenService = tokenService;
        this.restClient = RestClient.create();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> listEvents(String calendarId, ZonedDateTime start, ZonedDateTime end) {
        String token = tokenService.getAccessToken();

        String uri = UriComponentsBuilder
                .fromUriString(BASE_URL + "/calendars/{calendarId}/events")
                .queryParam("timeMin", start.format(DateTimeFormatter.ISO_INSTANT))
                .queryParam("timeMax", end.format(DateTimeFormatter.ISO_INSTANT))
                .queryParam("singleEvents", "true")
                .queryParam("orderBy", "startTime")
                .buildAndExpand(calendarId)
                .encode()
                .toUriString();

        try {
            return restClient.get()
                    .uri(uri)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            throw new GoogleIntegrationException("Impossible de récupérer les events : " + calendarId, e);
        }
    }

    public Map<String, Object> listEventsThisWeek(String calendarId) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay(now.getZone());
        return listEvents(calendarId, startOfWeek, startOfWeek.plusDays(7));
    }
}