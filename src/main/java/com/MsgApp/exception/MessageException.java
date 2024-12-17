package com.MsgApp.exception;

// Bu sınıf, mesaj işlemleri sırasında oluşabilecek hataları yönetir
// Örneğin: Mesaj boyutu limiti aşıldığında
public class MessageException extends MessageAppException {
    public MessageException(String message) {
        super(message);
    }
}
