public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    public void setFilled() {
        filled = true;
    }

    public boolean getFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
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
        Pipe p = new Pipe();
        if (rep == null) {
            return p;
        }
        switch (rep) {
            case "HZ": p.shape = PipeShape.HORIZONTAL; break;
            case "VT": p.shape = PipeShape.VERTICAL; break;
            case "TL": p.shape = PipeShape.TOP_LEFT; break;
            case "TR": p.shape = PipeShape.TOP_RIGHT; break;
            case "BL": p.shape = PipeShape.BOTTOM_LEFT; break;
            case "BR": p.shape = PipeShape.BOTTOM_RIGHT; break;
            case "CR": p.shape = PipeShape.CROSS; break;
            default: break;
        }
        return p;
    }
}
