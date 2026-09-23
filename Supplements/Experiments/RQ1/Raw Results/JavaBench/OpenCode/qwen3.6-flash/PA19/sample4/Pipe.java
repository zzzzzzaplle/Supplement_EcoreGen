import java.util.Arrays;
import java.util.List;

public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe(PipeShape shape) {
        this.shape = shape;
        this.filled = false;
    }
    public Pipe() {
        this.shape = PipeShape.HORIZONTAL;
        this.filled = false;
    }
    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return filled;
    }
    public void setShape(PipeShape shape) {
        this.shape = shape;
    }
    

    public Direction[] getConnections() {
        return switch (shape) {
            case HORIZONTAL -> new Direction[]{Direction.LEFT, Direction.RIGHT};
            case VERTICAL -> new Direction[]{Direction.UP, Direction.DOWN};
            case TOP_LEFT -> new Direction[]{Direction.UP, Direction.LEFT};
            case TOP_RIGHT -> new Direction[]{Direction.UP, Direction.RIGHT};
            case BOTTOM_LEFT -> new Direction[]{Direction.DOWN, Direction.LEFT};
            case BOTTOM_RIGHT -> new Direction[]{Direction.DOWN, Direction.RIGHT};
            case CROSS -> new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
        };
    }

    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public PipeShape getShape() {
        return shape;
    }

    public static Pipe fromString(String rep) {
        return switch (rep) {
            case "HZ" -> new Pipe(PipeShape.HORIZONTAL);
            case "VT" -> new Pipe(PipeShape.VERTICAL);
            case "TL" -> new Pipe(PipeShape.TOP_LEFT);
            case "TR" -> new Pipe(PipeShape.TOP_RIGHT);
            case "BL" -> new Pipe(PipeShape.BOTTOM_LEFT);
            case "BR" -> new Pipe(PipeShape.BOTTOM_RIGHT);
            case "CR" -> new Pipe(PipeShape.CROSS);
            default -> null;
        };
    }
}
