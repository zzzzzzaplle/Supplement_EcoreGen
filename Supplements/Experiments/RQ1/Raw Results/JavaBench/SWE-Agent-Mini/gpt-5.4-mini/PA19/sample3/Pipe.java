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
        return new Direction[0];
    }

    public char toSingleChar() {
        return shape == null ? '?' : shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        return new Pipe();
    }

    public PipeShape getShape() { return shape; }
    public void setShape(PipeShape shape) { this.shape = shape; }
    public void setFilled(boolean filled) { this.filled = filled; }
}
