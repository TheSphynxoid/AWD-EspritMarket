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

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(LocalDateTime.now(), status.value(), message));
    }

    public record ErrorResponse(LocalDateTime timestamp, int status, String message) {
    }
}
