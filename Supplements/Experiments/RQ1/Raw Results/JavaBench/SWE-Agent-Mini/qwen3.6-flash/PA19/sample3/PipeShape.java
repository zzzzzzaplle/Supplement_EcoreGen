import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of pipe shapes with connection directions and character rendering.
 */
public enum PipeShape {
    HORIZONTAL(
            new Direction[]{Direction.LEFT, Direction.RIGHT},
            PipePatterns.Filled.HORIZONTAL,
            PipePatterns.Unfilled.HORIZONTAL),
    VERTICAL(
            new Direction[]{Direction.UP, Direction.DOWN},
            PipePatterns.Filled.VERTICAL,
            PipePatterns.Unfilled.VERTICAL),
    TOP_LEFT(
            new Direction[]{Direction.UP, Direction.LEFT},
            PipePatterns.Filled.TOP_LEFT,
            PipePatterns.Unfilled.TOP_LEFT),
    TOP_RIGHT(
            new Direction[]{Direction.UP, Direction.RIGHT},
            PipePatterns.Filled.TOP_RIGHT,
            PipePatterns.Unfilled.TOP_RIGHT),
    BOTTOM_LEFT(
            new Direction[]{Direction.DOWN, Direction.LEFT},
            PipePatterns.Filled.BOTTOM_LEFT,
            PipePatterns.Unfilled.BOTTOM_LEFT),
    BOTTOM_RIGHT(
            new Direction[]{Direction.DOWN, Direction.RIGHT},
            PipePatterns.Filled.BOTTOM_RIGHT,
            PipePatterns.Unfilled.BOTTOM_RIGHT),
    CROSS(
            new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT},
            PipePatterns.Filled.CROSS,
            PipePatterns.Unfilled.CROSS);

    private final Direction[] connections;
    private final char filledChar;
    private final char unfilledChar;
    private static final Map<String, PipeShape> fromStringMap;

    static {
        fromStringMap = new HashMap<>();
        fromStringMap.put("HZ", HORIZONTAL);
        fromStringMap.put("VT", VERTICAL);
        fromStringMap.put("TL", TOP_LEFT);
        fromStringMap.put("TR", TOP_RIGHT);
        fromStringMap.put("BL", BOTTOM_LEFT);
        fromStringMap.put("BR", BOTTOM_RIGHT);
        fromStringMap.put("CR", CROSS);
    }

    PipeShape(Direction[] connections, char filledChar, char unfilledChar) {
        this.connections = connections;
        this.filledChar = filledChar;
        this.unfilledChar = unfilledChar;
    }

    public char getCharByState(boolean isFilled) {
        return isFilled ? filledChar : unfilledChar;
    }

    public Direction[] getConnections() {
        return connections;
    }

    public static PipeShape fromString(String rep) {
        PipeShape shape = fromStringMap.get(rep);
        if (shape == null) {
            throw new IllegalArgumentException("Unknown pipe shape: " + rep);
        }
        return shape;
    }
}
