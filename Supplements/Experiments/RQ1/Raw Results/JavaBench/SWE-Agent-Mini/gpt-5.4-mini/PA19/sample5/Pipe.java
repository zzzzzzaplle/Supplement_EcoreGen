public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
    }

    public void setFilled() {
        filled = true;
    }

    public boolean getFilled() {
        return filled;
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
                return new Direction[0];
        }
    }

    public char toSingleChar() {
        return shape == null ? '?' : shape.getCharByState(filled);
    }

public static Pipe fromString(String rep) {
        PipeShape shape;
        switch (rep) {
            case "HZ": shape = PipeShape.HORIZONTAL; break;
            case "VT": shape = PipeShape.VERTICAL; break;
            case "TL": shape = PipeShape.TOP_LEFT; break;
            case "TR": shape = PipeShape.TOP_RIGHT; break;
            case "BL": shape = PipeShape.BOTTOM_LEFT; break;
            case "BR": shape = PipeShape.BOTTOM_RIGHT; break;
            case "CR": shape = PipeShape.CROSS; break;
            default: shape = PipeShape.CROSS; break;
        }
        return new Pipe(shape);
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
}
