package cn.hill4j.rpcext.core.rpcext.exception;

/**
 * 2019/9/18 09:58<br>
 * Description: package-info扫描异常
 *
 * @author hillchen
 */
public class PackageInfoScanException extends RuntimeException {
    public PackageInfoScanException() {
    }

    public PackageInfoScanException(String message) {
        super(message);
    }

    public PackageInfoScanException(String message, Throwable cause) {
        super(message, cause);
    }

    public PackageInfoScanException(Throwable cause) {
        super(cause);
    }

    public PackageInfoScanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
