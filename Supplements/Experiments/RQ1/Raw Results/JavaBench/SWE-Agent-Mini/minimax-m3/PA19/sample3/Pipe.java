import java.util.Objects;

/**
 * Pipe placed on a FillableCell; carries a shape and a filled state.
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

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    public boolean getFilled() {
        return filled;
    }

    public void setFilled() {
        this.filled = true;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilledState(boolean filled) {
        this.filled = filled;
    }

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
                return new Direction[]{Direction.UP, Direction.RIGHT};
            case TOP_RIGHT:
                return new Direction[]{Direction.UP, Direction.LEFT};
            case BOTTOM_LEFT:
                return new Direction[]{Direction.DOWN, Direction.RIGHT};
            case BOTTOM_RIGHT:
                return new Direction[]{Direction.DOWN, Direction.LEFT};
            case CROSS:
                return new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
            default:
                throw new IllegalStateException("Unknown shape");
        }
    }

    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        if (rep == null) {
            return null;
        }
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
                return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Pipe)) {
            return false;
        }
        Pipe other = (Pipe) obj;
        return this.shape == other.shape && this.filled == other.filled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shape, filled);
    }
}
