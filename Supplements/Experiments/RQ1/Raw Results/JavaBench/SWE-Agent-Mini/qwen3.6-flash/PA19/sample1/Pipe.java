import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * A pipe piece with a shape and filled state.
 */
public class Pipe implements MapElement {

    private PipeShape shape;
    private boolean filled;

    private static final Map<String, PipeShape> shapeMap = new HashMap<>();

    static {
        shapeMap.put("HZ", PipeShape.HORIZONTAL);
        shapeMap.put("VT", PipeShape.VERTICAL);
        shapeMap.put("TL", PipeShape.TOP_LEFT);
        shapeMap.put("TR", PipeShape.TOP_RIGHT);
        shapeMap.put("BL", PipeShape.BOTTOM_LEFT);
        shapeMap.put("BR", PipeShape.BOTTOM_RIGHT);
        shapeMap.put("CR", PipeShape.CROSS);
    }

    public Pipe() {
    }

    public Pipe(PipeShape shape, boolean filled) {
        this.shape = shape;
        this.filled = filled;
    }

    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return filled;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    public Direction[] getConnections() {
        return shape.getConnections();
    }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        PipeShape shape = shapeMap.get(rep);
        if (shape == null) {
            throw new IllegalArgumentException("Unknown pipe shape: " + rep);
        }
        return new Pipe(shape, false);
    }
}
