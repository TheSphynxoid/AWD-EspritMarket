package net.thesphynx.srv.exception;

import org.springframework.http.HttpStatus;

/** Erreur métier avec son statut HTTP (404, 403, 409...). */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }

    public static ApiException notFound(String message) { return new ApiException(HttpStatus.NOT_FOUND, message); }
    public static ApiException forbidden(String message) { return new ApiException(HttpStatus.FORBIDDEN, message); }
    public static ApiException conflict(String message) { return new ApiException(HttpStatus.CONFLICT, message); }
}
