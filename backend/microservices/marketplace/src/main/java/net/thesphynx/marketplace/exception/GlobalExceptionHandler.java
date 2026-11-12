package net.thesphynx.marketplace.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataAccessException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        return response(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return response(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        return response(HttpStatus.BAD_REQUEST, "Le corps JSON est invalide. Vérifiez notamment que categoryId est un nombre.");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabase(DataAccessException ex) {
        LOGGER.error("Erreur d'accès à la base marketplace", ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la lecture des données marketplace. Consultez les logs du service.");
    }

    /** Erreur renvoyée par un autre microservice (Feign) : on relaie un 4xx tel quel, sinon 502. */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeign(FeignException ex) {
        String detail = ex.contentUTF8();
        if (ex.status() >= 400 && ex.status() < 500) {
            HttpStatus status = HttpStatus.resolve(ex.status());
            return response(status != null ? status : HttpStatus.BAD_REQUEST,
                    "Requête refusée par le service distant : " + (detail == null || detail.isBlank() ? ex.getMessage() : detail));
        }
        LOGGER.error("Appel inter-service en échec", ex);
        return response(HttpStatus.BAD_GATEWAY, "Service distant indisponible, réessayez plus tard.");
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(LocalDateTime.now(), status.value(), message));
    }

    public record ErrorResponse(LocalDateTime timestamp, int status, String message) {
    }
}
