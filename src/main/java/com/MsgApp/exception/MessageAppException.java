package com.MsgApp.exception;

// Ana exception sınıfımız - diğer tüm exception'lar bundan türeyecek
public class MessageAppException extends RuntimeException {
    public MessageAppException(String message) {
        super(message);
    }

    public MessageAppException(String message, Throwable cause) {
        super(message, cause);
    }
}

