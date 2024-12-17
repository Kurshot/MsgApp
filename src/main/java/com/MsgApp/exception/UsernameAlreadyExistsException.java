package com.MsgApp.exception;

// Bu sınıf, kullanıcı kaydı sırasında aynı username veya
// email ile kayıt yapılmaya çalışıldığında kullanılır
public class UsernameAlreadyExistsException extends MessageAppException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
