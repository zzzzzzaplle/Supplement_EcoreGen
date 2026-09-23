/**
 * A pipe with a shape and filled state.
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
     * Sets this pipe as filled.
     */
    public void setFilled() {
        this.filled = true;
    }

    /**
     * Returns whether this pipe is filled.
     */
    public boolean getFilled() {
        return filled;
    }

    /**
     * Returns the connection directions based on the pipe shape.
     */
    public Direction[] getConnections() {
        if (shape == null) {
            throw new IllegalStateException("Unknown shape");
        }
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
     */
    public static Pipe fromString(String rep) {
        PipeShape s;
        switch (rep) {
            case "HZ":
                s = PipeShape.HORIZONTAL;
                break;
            case "VT":
                s = PipeShape.VERTICAL;
                break;
            case "TL":
                s = PipeShape.TOP_LEFT;
                break;
            case "TR":
                s = PipeShape.TOP_RIGHT;
                break;
            case "BL":
                s = PipeShape.BOTTOM_LEFT;
                break;
            case "BR":
                s = PipeShape.BOTTOM_RIGHT;
                break;
            case "CR":
                s = PipeShape.CROSS;
                break;
            default:
                throw new IllegalArgumentException("Unknown pipe representation: " + rep);
        }
        return new Pipe(s);
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }
}
