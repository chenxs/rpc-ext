package com.github.chenxs.rpc.ext.core.utils.exception;

/**
 * 〈一句话功能简述〉<br>
 * Description: ReflectException
 *
 * @author hillchen
 * @create 2019/8/30 01:05
 */
public class ReflectException extends RuntimeException {
    public ReflectException() {
    }

    public ReflectException(String message) {
        super(message);
    }

    public ReflectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectException(Throwable cause) {
        super(cause);
    }

    public ReflectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}