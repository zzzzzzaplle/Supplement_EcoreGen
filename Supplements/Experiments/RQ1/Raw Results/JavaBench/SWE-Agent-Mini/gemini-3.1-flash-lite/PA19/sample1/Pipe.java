public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {}
    public Pipe(PipeShape shape) { this.shape = shape; }

    public PipeShape getShape() { return shape; }
    public void setShape(PipeShape shape) { this.shape = shape; }
    public boolean getFilled() { return filled; }
    public void setFilled(boolean filled) { this.filled = filled; }
    public void setFilled() { this.filled = true; }

    public Direction[] getConnections() { return new Direction[0]; }
    public char toSingleChar() { return shape.getCharByState(filled); }
    public static Pipe fromString(String rep) { return new Pipe(); }
}
