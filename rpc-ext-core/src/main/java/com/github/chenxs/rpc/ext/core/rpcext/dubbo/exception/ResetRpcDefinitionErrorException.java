package com.github.chenxs.rpc.ext.core.rpcext.dubbo.exception;

/**
 * 〈一句话功能简述〉<br>
 * Description: exception.ResetRpcDefinitionErrorException
 *
 * @author hillchen
 * @create 2019/8/29 16:50
 */
public class ResetRpcDefinitionErrorException extends RuntimeException {
    public ResetRpcDefinitionErrorException() {
    }

    public ResetRpcDefinitionErrorException(String message) {
        super(message);
    }

    public ResetRpcDefinitionErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResetRpcDefinitionErrorException(Throwable cause) {
        super(cause);
    }

    public ResetRpcDefinitionErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}