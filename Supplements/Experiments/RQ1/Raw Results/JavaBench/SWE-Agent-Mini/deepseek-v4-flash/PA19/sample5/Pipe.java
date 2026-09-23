/**
 * Represents a pipe with a shape and filled state.
 */
public class Pipe implements MapElement {

    private PipeShape shape;
    private boolean filled;

    public Pipe() {
        this.shape = null;
        this.filled = false;
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
        this.filled = false;
    }

    /**
     * Marks this pipe as filled.
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

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    /**
     * Returns the connection directions based on the pipe shape.
     *
     * @return an array of Direction values representing connections
     * @throws IllegalStateException if the shape is unknown
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
     * Creates a Pipe from a string code.
     *
     * @param rep the string code (HZ, VT, TL, TR, BL, BR, CR)
     * @return the created Pipe
     */
    public static Pipe fromString(String rep) {
        PipeShape shape;
        switch (rep) {
            case "HZ":
                shape = PipeShape.HORIZONTAL;
                break;
            case "VT":
                shape = PipeShape.VERTICAL;
                break;
            case "TL":
                shape = PipeShape.TOP_LEFT;
                break;
            case "TR":
                shape = PipeShape.TOP_RIGHT;
                break;
            case "BL":
                shape = PipeShape.BOTTOM_LEFT;
                break;
            case "BR":
                shape = PipeShape.BOTTOM_RIGHT;
                break;
            case "CR":
                shape = PipeShape.CROSS;
                break;
            default:
                throw new IllegalArgumentException("Unknown pipe code: " + rep);
        }
        return new Pipe(shape);
    }
}
