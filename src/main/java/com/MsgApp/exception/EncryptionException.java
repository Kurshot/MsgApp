package com.MsgApp.exception;

// Bu sınıf, mesajların şifrelenmesi sırasında
// oluşabilecek hataları yönetir
public class EncryptionException extends MessageAppException {
    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
