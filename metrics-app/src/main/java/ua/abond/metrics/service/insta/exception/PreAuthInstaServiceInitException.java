package ua.abond.metrics.service.insta.exception;

public class PreAuthInstaServiceInitException extends RuntimeException {

    public PreAuthInstaServiceInitException(String message) {
        super(message);
    }

    public PreAuthInstaServiceInitException(Throwable cause) {
        super(cause);
    }

    public PreAuthInstaServiceInitException(String message, Throwable cause) {
        super(message, cause);
    }

}
