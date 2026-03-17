package com.CampusRoomStatus.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Salle non trouvée"),
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Événement non trouvé"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Paramètres invalides"),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Rate limit atteint"),
    GOOGLE_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service Google indisponible"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur de base de données"),
    DATABASE_ERROR_FORBIDDEN(HttpStatus.FORBIDDEN, "Accès à la base de données interdit"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Accès interdit");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}