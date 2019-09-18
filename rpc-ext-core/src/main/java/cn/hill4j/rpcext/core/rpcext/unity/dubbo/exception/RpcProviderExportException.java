package cn.hill4j.rpcext.core.rpcext.unity.dubbo.exception;

/**
 * 2019/9/15 17:32<br>
 * Description: rpc服务生产者暴露异常
 *
 * @author hillchen
 */
public class RpcProviderExportException extends RuntimeException {

    public RpcProviderExportException() {
    }

    public RpcProviderExportException(String message) {
        super(message);
    }

    public RpcProviderExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcProviderExportException(Throwable cause) {
        super(cause);
    }

    public RpcProviderExportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
