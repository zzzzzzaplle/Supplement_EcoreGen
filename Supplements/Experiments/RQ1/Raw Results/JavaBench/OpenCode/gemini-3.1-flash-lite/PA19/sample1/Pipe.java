import java.util.Optional;

public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {}

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public boolean getFilled() {
        return filled;
    }

    public Direction[] getConnections() {
        return new Direction[0]; // Placeholder
    }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        return new Pipe(); // Placeholder
    }
}
