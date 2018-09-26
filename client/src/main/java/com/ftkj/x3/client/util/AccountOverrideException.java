package com.ftkj.x3.client.util;

/**
 * @author luch
 */
public class AccountOverrideException extends RuntimeException {
    public AccountOverrideException() {
        super();
    }

    public AccountOverrideException(String message) {
        super(message);
    }

    public AccountOverrideException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountOverrideException(Throwable cause) {
        super(cause);
    }

    protected AccountOverrideException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
