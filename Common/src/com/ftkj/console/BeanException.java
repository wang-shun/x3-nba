package com.ftkj.console;

/**
 * @author luch
 */
public class BeanException extends RuntimeException {
    public BeanException() {
    }

    public BeanException(String message) {
        super(message);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanException(Throwable cause) {
        super(cause);
    }

    public BeanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static BeanException exception(String msg, Object... args) {
        throw new BeanException(String.format(msg, args));
    }

    public static BeanException exception(String msg) {
        throw new BeanException(msg);
    }
}
