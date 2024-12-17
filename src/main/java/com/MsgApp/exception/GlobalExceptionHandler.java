package com.MsgApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice  // Bu anotasyon, bu sınıfın tüm controller'lar için exception handler olduğunu belirtir
public class GlobalExceptionHandler {

    // Hata yanıtı oluşturan yardımcı metod
    private ResponseEntity<Object> createErrorResponse(
            String message,
            HttpStatus status,
            WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(
            UsernameAlreadyExistsException ex, WebRequest request) {
        return createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<Object> handleEncryptionException(
            EncryptionException ex, WebRequest request) {
        return createErrorResponse(
                "Şifreleme işlemi sırasında bir hata oluştu",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    @ExceptionHandler(GroupException.class)
    public ResponseEntity<Object> handleGroupException(
            GroupException ex, WebRequest request) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(HashingException.class)
    public ResponseEntity<Object> handleHashingException(
            HashingException ex, WebRequest request) {
        return createErrorResponse(
                "Hash hesaplama işlemi sırasında bir hata oluştu",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    // Genel hata yakalayıcı - diğer handler'ların yakalamadığı tüm exception'lar için
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception ex, WebRequest request) {
        return createErrorResponse(
                "Beklenmeyen bir hata oluştu",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }
}
