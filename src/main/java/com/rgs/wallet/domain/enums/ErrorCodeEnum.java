package com.rgs.wallet.domain.enums;

import java.util.Locale;
import java.util.ResourceBundle;

public enum ErrorCodeEnum {
    WS400001,
    WS400002,
    WS404001,
    WS404002,
    WS409001,
    WS409002,
    WS409003,
    WS429001,
    WS500001;

    public String getMessage(final Locale messageLocale){
        return ResourceBundle.getBundle("messages/exceptions", messageLocale).getString(this.name()+".message");
    }
}
