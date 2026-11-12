package net.thesphynx.srv.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** Erreurs JSON homogènes : { timestamp, status, message, errors{champ: message} }. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public record ApiError(LocalDateTime timestamp, int status, String message, Map<String, String> errors) {
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex) {
        return error(ex.getStatus(), ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Données invalides", errors);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleUnreadable(Exception ex) {
        return error(HttpStatus.BAD_REQUEST, "Requête mal formée (JSON, date ou valeur invalide)", null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "Ressource introuvable", null);
    }

    /** Laisse Spring Security répondre 401 / 403 normalement. */
    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public void rethrowSecurity(RuntimeException ex) {
        throw ex;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex) {
        LOGGER.error("Erreur inattendue", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du service", null);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, Map<String, String> errors) {
        return ResponseEntity.status(status).body(new ApiError(LocalDateTime.now(), status.value(), message, errors));
    }
}
