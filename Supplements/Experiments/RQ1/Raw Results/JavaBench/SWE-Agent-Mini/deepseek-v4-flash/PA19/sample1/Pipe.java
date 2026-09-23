import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pipe with a shape and fill state.
 */
public class Pipe implements MapElement {

    private PipeShape shape;
    private boolean filled;

    public Pipe() {
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
        this.filled = false;
    }

    /**
     * Marks this pipe as filled with water.
     */
    public void setFilled() {
        this.filled = true;
    }

    /**
     * Returns whether this pipe is filled.
     *
     * @return true if filled, false otherwise
     */
    public boolean getFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    /**
     * Returns the connection directions based on the pipe shape.
     *
     * @return an array of Direction representing connections
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

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    /**
     * Creates a Pipe from a short string code.
     *
     * @param rep the string code (HZ, VT, TL, TR, BL, BR, CR)
     * @return a new Pipe instance
     */
    public static Pipe fromString(String rep) {
        switch (rep.trim().toUpperCase()) {
            case "HZ":
                return new Pipe(PipeShape.HORIZONTAL);
            case "VT":
                return new Pipe(PipeShape.VERTICAL);
            case "TL":
                return new Pipe(PipeShape.TOP_LEFT);
            case "TR":
                return new Pipe(PipeShape.TOP_RIGHT);
            case "BL":
                return new Pipe(PipeShape.BOTTOM_LEFT);
            case "BR":
                return new Pipe(PipeShape.BOTTOM_RIGHT);
            case "CR":
                return new Pipe(PipeShape.CROSS);
            default:
                throw new IllegalArgumentException("Unknown pipe code: " + rep);
        }
    }
}
