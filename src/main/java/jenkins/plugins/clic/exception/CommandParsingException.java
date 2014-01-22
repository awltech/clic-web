package jenkins.plugins.clic.exception;

/**
 * User: pierremarot
 * Date: 17/01/2014
 * Time: 17:31
 */
public class CommandParsingException extends Exception{

    public CommandParsingException() {
        super();
    }

    public CommandParsingException(String message) {
        super(message);
    }

    public CommandParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandParsingException(Throwable cause) {
        super(cause);
    }

    protected CommandParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
