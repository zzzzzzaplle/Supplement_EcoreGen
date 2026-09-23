import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents a pipe placed on a {@link FillableCell}.
 */
public class Pipe implements MapElement {

    private  PipeShape shape;
    private  boolean filled;

    public Pipe() {
    }

    public Pipe(PipeShape shape, boolean filled) {
        this.shape = shape;
        this.filled = filled;
    }

    private static final Map<String, PipeShape> STRING_TO_SHAPE;

    static {
        STRING_TO_SHAPE = new HashMap<>();
        STRING_TO_SHAPE.put("HZ", PipeShape.HORIZONTAL);
        STRING_TO_SHAPE.put("VT", PipeShape.VERTICAL);
        STRING_TO_SHAPE.put("TL", PipeShape.TOP_LEFT);
        STRING_TO_SHAPE.put("TR", PipeShape.TOP_RIGHT);
        STRING_TO_SHAPE.put("BL", PipeShape.BOTTOM_LEFT);
        STRING_TO_SHAPE.put("BR", PipeShape.BOTTOM_RIGHT);
        STRING_TO_SHAPE.put("CR", PipeShape.CROSS);
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return filled;
    }

    /**
     * Returns the list of connection directions based on the current pipe shape.
     */
    public Direction[] getConnections() {
        switch (shape) {
            case HORIZONTAL:
                return new Direction[]{Direction.LEFT, Direction.RIGHT};
            case VERTICAL:
                return new Direction[]{Direction.UP, Direction.DOWN};
            case TOP_LEFT:
                return new Direction[]{Direction.UP, Direction.LEFT};
            case TOP_RIGHT:
                return new Direction[]{Direction.UP, Direction.RIGHT};
            case BOTTOM_LEFT:
                return new Direction[]{Direction.DOWN, Direction.LEFT};
            case BOTTOM_RIGHT:
                return new Direction[]{Direction.DOWN, Direction.RIGHT};
            case CROSS:
                return new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
            default:
                throw new IllegalStateException("Unknown shape");
        }
    }

    /**
     * Returns the character representation of this pipe.
     */
    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    /**
     * Constructs a Pipe from a short string code.
     */
    public static Pipe fromString(String rep) {
        PipeShape shape = STRING_TO_SHAPE.get(rep.toUpperCase());
        if (shape == null) {
            throw new IllegalArgumentException("Unknown pipe shape code: " + rep);
        }
        return new Pipe(shape, false);
    }
}
