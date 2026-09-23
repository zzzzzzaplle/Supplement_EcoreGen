public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
    }

    public void setFilled() {
        this.filled = true;
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
        Pipe p = new Pipe();
        if (rep == null) return p;
        String s = rep.trim().toUpperCase();
        if (s.equals("HZ")) p.shape = PipeShape.HORIZONTAL;
        else if (s.equals("VT")) p.shape = PipeShape.VERTICAL;
        else if (s.equals("TL")) p.shape = PipeShape.TOP_LEFT;
        else if (s.equals("TR")) p.shape = PipeShape.TOP_RIGHT;
        else if (s.equals("BL")) p.shape = PipeShape.BOTTOM_LEFT;
        else if (s.equals("BR")) p.shape = PipeShape.BOTTOM_RIGHT;
        else if (s.equals("CR")) p.shape = PipeShape.CROSS;
        return p;
    }

    public PipeShape getShape() { return shape; }
    public void setShape(PipeShape shape) { this.shape = shape; }
    public boolean isFilled() { return filled; }
    public void setFilledValue(boolean filled) { this.filled = filled; }
}
