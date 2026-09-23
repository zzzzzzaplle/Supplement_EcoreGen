import java.util.Arrays;

public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
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

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public Direction[] getConnections() {
        switch (shape) {
            case HORIZONTAL:
                return new Direction[] {Direction.LEFT, Direction.RIGHT};
            case VERTICAL:
                return new Direction[] {Direction.UP, Direction.DOWN};
            case TOP_LEFT:
                return new Direction[] {Direction.UP, Direction.LEFT};
            case TOP_RIGHT:
                return new Direction[] {Direction.UP, Direction.RIGHT};
            case BOTTOM_LEFT:
                return new Direction[] {Direction.DOWN, Direction.LEFT};
            case BOTTOM_RIGHT:
                return new Direction[] {Direction.DOWN, Direction.RIGHT};
            case CROSS:
                return new Direction[] {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
            default:
                return new Direction[0];
        }
    }

    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        PipeShape shape = null;
        if ("HZ".equals(rep)) {
            shape = PipeShape.HORIZONTAL;
        } else if ("VT".equals(rep)) {
            shape = PipeShape.VERTICAL;
        } else if ("TL".equals(rep)) {
            shape = PipeShape.TOP_LEFT;
        } else if ("TR".equals(rep)) {
            shape = PipeShape.TOP_RIGHT;
        } else if ("BL".equals(rep)) {
            shape = PipeShape.BOTTOM_LEFT;
        } else if ("BR".equals(rep)) {
            shape = PipeShape.BOTTOM_RIGHT;
        } else if ("CR".equals(rep)) {
            shape = PipeShape.CROSS;
        }
        return new Pipe(shape);
    }
}
