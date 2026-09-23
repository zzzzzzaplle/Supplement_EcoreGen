public class ShouldNotReachException extends RuntimeException {
    public ShouldNotReachException() {
        super("This branch should not be reached.");
    }
}
