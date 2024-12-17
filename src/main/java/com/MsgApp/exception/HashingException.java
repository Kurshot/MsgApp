package com.MsgApp.exception;

// Bu sınıf, hash hesaplama işlemleri sırasında
// oluşabilecek hataları yönetir
public class HashingException extends MessageAppException {
    public HashingException(String message) {
        super(message);
    }

    public HashingException(String message, Throwable cause) {
        super(message, cause);
    }
}
