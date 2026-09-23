/**
 * Thrown when a branch should not be reached. Used to avoid compilation error.
 */
class ShouldNotReachException extends RuntimeException {

    public ShouldNotReachException() {
        super("This branch should not be reached.");
    }
}
