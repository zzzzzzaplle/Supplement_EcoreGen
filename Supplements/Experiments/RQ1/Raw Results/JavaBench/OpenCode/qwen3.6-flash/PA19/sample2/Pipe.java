import java.util.Arrays;

public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
        this.shape = PipeShape.HORIZONTAL;
        this.filled = false;
    }

    public Pipe(PipeShape shape, boolean filled) {
        this.shape = shape;
        this.filled = filled;
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

    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        return switch (rep) {
            case "HZ" -> new Pipe(PipeShape.HORIZONTAL, false);
            case "VT" -> new Pipe(PipeShape.VERTICAL, false);
            case "TL" -> new Pipe(PipeShape.TOP_LEFT, false);
            case "TR" -> new Pipe(PipeShape.TOP_RIGHT, false);
            case "BL" -> new Pipe(PipeShape.BOTTOM_LEFT, false);
            case "BR" -> new Pipe(PipeShape.BOTTOM_RIGHT, false);
            case "CR" -> new Pipe(PipeShape.CROSS, false);
            default -> throw new IllegalArgumentException("Unknown pipe shape: " + rep);
        };
    }
}
