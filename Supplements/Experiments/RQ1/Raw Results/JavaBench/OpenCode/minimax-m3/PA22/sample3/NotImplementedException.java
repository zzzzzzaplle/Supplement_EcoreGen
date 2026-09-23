/**
 * Throw to indicate that the feature is not implemented.
 */
public class NotImplementedException extends RuntimeException {

    public NotImplementedException() {
        super();
    }

    public NotImplementedException(String message) {
        super(message);
    }
}
