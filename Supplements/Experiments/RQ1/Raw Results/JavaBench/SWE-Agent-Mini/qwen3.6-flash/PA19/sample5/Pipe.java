import java.util.*;

/**
 * A pipe that can be placed on a FillableCell.
 */
public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
        this.shape = PipeShape.HORIZONTAL;
        this.filled = false;
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
        this.filled = false;
    }

    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return filled;
    }

    public void setFilledState(boolean filled) {
        this.filled = filled;
    }

    public boolean isFilled() {
        return this.filled;
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
     * Creates a Pipe from a short string code.
     * Codes: HZ=HORIZONTAL, VT=VERTICAL, TL=TOP_LEFT, TR=TOP_RIGHT,
     *        BL=BOTTOM_LEFT, BR=BOTTOM_RIGHT, CR=CROSS
     */
    public static Pipe fromString(String rep) {
        Pipe pipe = new Pipe();
        switch (rep) {
            case "HZ":
                pipe.shape = PipeShape.HORIZONTAL;
                break;
            case "VT":
                pipe.shape = PipeShape.VERTICAL;
                break;
            case "TL":
                pipe.shape = PipeShape.TOP_LEFT;
                break;
            case "TR":
                pipe.shape = PipeShape.TOP_RIGHT;
                break;
            case "BL":
                pipe.shape = PipeShape.BOTTOM_LEFT;
                break;
            case "BR":
                pipe.shape = PipeShape.BOTTOM_RIGHT;
                break;
            case "CR":
                pipe.shape = PipeShape.CROSS;
                break;
            default:
                throw new IllegalArgumentException("Unknown pipe shape: " + rep);
        }
        return pipe;
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }
}
