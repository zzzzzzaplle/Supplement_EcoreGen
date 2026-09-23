public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {}

    public void setFilled() { this.filled = true; }
    public boolean getFilled() { return filled; }
    
    public PipeShape getShape() { return shape; }
    public void setShape(PipeShape shape) { this.shape = shape; }

    public Direction[] getConnections() { return new Direction[0]; }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        return new Pipe();
    }
}
