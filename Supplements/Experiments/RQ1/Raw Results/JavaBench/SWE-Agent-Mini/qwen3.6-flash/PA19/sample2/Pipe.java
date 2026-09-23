import java.util.*;

public class Pipe implements MapElement {

    private PipeShape shape;
    private boolean filled;

    public Pipe() {
        this.shape = null;
        this.filled = false;
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
        this.filled = false;
    }

    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return filled;
    }

    public Direction[] getConnections() {
        return shape.getConnections();
    }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        switch (rep) {
            case "HZ":
                return new Pipe(PipeShape.HORIZONTAL);
            case "VT":
                return new Pipe(PipeShape.VERTICAL);
            case "TL":
                return new Pipe(PipeShape.TOP_LEFT);
            case "TR":
                return new Pipe(PipeShape.TOP_RIGHT);
            case "BL":
                return new Pipe(PipeShape.BOTTOM_LEFT);
            case "BR":
                return new Pipe(PipeShape.BOTTOM_RIGHT);
            case "CR":
                return new Pipe(PipeShape.CROSS);
            default:
                return null;
        }
    }
}
