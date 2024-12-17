package com.MsgApp.exception;

// Bu sınıf, kullanıcı bulunamadığında kullanılır
// Örneğin: Mesaj gönderirken alıcı bulunamadığında
public class UserNotFoundException extends MessageAppException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
