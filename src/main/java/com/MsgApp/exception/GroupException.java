package com.MsgApp.exception;

// Bu sınıf, grup işlemleri sırasında oluşabilecek hataları yönetir
// Örneğin: Maksimum üye sayısı aşıldığında
public class GroupException extends MessageAppException {
    public GroupException(String message) {
        super(message);
    }
}
