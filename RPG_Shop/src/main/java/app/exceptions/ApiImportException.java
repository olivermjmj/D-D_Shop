package app.exceptions;

public class ApiImportException extends RuntimeException {

    public ApiImportException(String message) {
    }

    public ApiImportException(String message, Throwable cause) {

        super(message, cause);
    }
}