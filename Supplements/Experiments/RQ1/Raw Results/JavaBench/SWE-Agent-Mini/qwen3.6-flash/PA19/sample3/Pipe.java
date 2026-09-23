import java.util.HashMap;
import java.util.Map;

/**
 * A pipe placed on a FillableCell in the map.
 */
public class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

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

    public Direction[] getConnections() {
        return shape.getConnections();
    }

    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    private static final Map<String, PipeShape> shapeMap = new HashMap<>();
    private static final Map<PipeShape, String> stringMap = new HashMap<>();

    static {
        shapeMap.put("HZ", PipeShape.HORIZONTAL);
        shapeMap.put("VT", PipeShape.VERTICAL);
        shapeMap.put("TL", PipeShape.TOP_LEFT);
        shapeMap.put("TR", PipeShape.TOP_RIGHT);
        shapeMap.put("BL", PipeShape.BOTTOM_LEFT);
        shapeMap.put("BR", PipeShape.BOTTOM_RIGHT);
        shapeMap.put("CR", PipeShape.CROSS);

        stringMap.put(PipeShape.HORIZONTAL, "HZ");
        stringMap.put(PipeShape.VERTICAL, "VT");
        stringMap.put(PipeShape.TOP_LEFT, "TL");
        stringMap.put(PipeShape.TOP_RIGHT, "TR");
        stringMap.put(PipeShape.BOTTOM_LEFT, "BL");
        stringMap.put(PipeShape.BOTTOM_RIGHT, "BR");
        stringMap.put(PipeShape.CROSS, "CR");
    }

    public static Pipe fromString(String rep) {
        PipeShape shape = shapeMap.get(rep);
        if (shape == null) {
            throw new IllegalArgumentException("Unknown pipe shape pattern: " + rep);
        }
        return new Pipe(shape, false);
    }

    public String toStringRep() {
        String s = stringMap.get(shape);
        if (s == null) {
            throw new NullPointerException("Shape mapping null");
        }
        return s;
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }
}
