import java.util.*;

/**
 * Represents a pipe placed on the map.
 */
public class Pipe implements MapElement {

    private PipeShape shape;
    private boolean filled;

    public Pipe() {
        this(PipeShape.HORIZONTAL, false);
    }

    public Pipe(PipeShape shape, boolean filled) {
        this.shape = shape;
        this.filled = filled;
    }

    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return this.filled;
    }

    public Direction[] getConnections() {
        return shape.getConnections();
    }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        PipeShape shape = PipeShape.fromString(rep);
        return new Pipe(shape, false);
    }
}
