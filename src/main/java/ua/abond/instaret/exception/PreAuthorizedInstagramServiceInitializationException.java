package ua.abond.instaret.exception;

public class PreAuthorizedInstagramServiceInitializationException extends RuntimeException {

    public PreAuthorizedInstagramServiceInitializationException(String message) {
        super(message);
    }

    public PreAuthorizedInstagramServiceInitializationException(Throwable cause) {
        super(cause);
    }

    public PreAuthorizedInstagramServiceInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
