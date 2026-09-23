public class Pa19Helper {

    private Pa19Helper() {
    }

    public static Coordinate getOffset(Direction dir) {
        return dir.getOffset();
    }

    public static Direction getOpposite(Direction dir) {
        return dir.getOpposite();
    }
}
